package com.ryland.netty.example11.client;

import com.ryland.netty.example11.client.handler.RpcResponseMessageHandler;
import com.ryland.netty.example11.message.RpcRequestMessage;
import com.ryland.netty.example11.protocol.MessageCodecSharable;
import com.ryland.netty.example11.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryland
 */
@Slf4j
public class RpcClient {

    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
    private static final RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Channel channel = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProcotolFrameDecoder())
                                    .addLast(LOGGING_HANDLER)
                                    .addLast(MESSAGE_CODEC)
                                    .addLast(RPC_HANDLER);
                        }
                    })
                    .connect("localhost", 8080).sync().channel();

            channel.writeAndFlush(new RpcRequestMessage(
                            1,
                            "com.ryland.netty.example11.server.service.HelloService",
                            "sayHello",
                            String.class,
                            new Class[]{String.class},
                            new Object[]{"Ryland"}
                    ))
                    .addListener(promise -> {
                        if (!promise.isSuccess()) {
                            Throwable cause = promise.cause();
                            log.error("error: [{}]", cause.toString());
                        }
                    });

            channel.closeFuture().sync();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
