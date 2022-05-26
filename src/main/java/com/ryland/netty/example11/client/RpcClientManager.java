package com.ryland.netty.example11.client;

import com.ryland.netty.example11.client.handler.RpcResponseMessageHandler;
import com.ryland.netty.example11.message.RpcRequestMessage;
import com.ryland.netty.example11.protocol.MessageCodecSharable;
import com.ryland.netty.example11.protocol.ProcotolFrameDecoder;
import com.ryland.netty.example11.protocol.SequenceIdGenerator;
import com.ryland.netty.example11.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;

/**
 * @author Ryland
 */
@Slf4j
public class RpcClientManager {

    private static Channel channel = null;

    private static final Object LOCK = new Object();

    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);

    private static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

    private static final RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();

    @SuppressWarnings("unchecked")
    public static <T> T getProxyService(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                (proxy, method, args) -> {
                    int sequenceId = SequenceIdGenerator.nextId();
                    RpcRequestMessage msg = new RpcRequestMessage(
                            sequenceId,
                            serviceClass.getName(),
                            method.getName(),
                            method.getReturnType(),
                            method.getParameterTypes(),
                            args
                    );
                    getChannel().writeAndFlush(msg);

                    DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
                    RpcResponseMessageHandler.putIntoPromises(sequenceId, promise);

                    promise.await();
                    if (promise.isSuccess()) {
                        return promise.getNow();
                    } else {
                        throw new IllegalStateException(promise.cause());
                    }
                });
    }

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ProcotolFrameDecoder());
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("client error", e);
        }
    }

    public static void main(String[] args) {
        getProxyService(HelloService.class).sayHello("Ryland");
        getProxyService(HelloService.class).sayHello("Sherry");
        getProxyService(HelloService.class).sayHello("Fan");
    }
}
