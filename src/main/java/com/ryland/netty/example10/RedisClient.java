package com.ryland.netty.example10;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author Ryland
 */
@Slf4j
public class RedisClient {

    private static final byte[] LINE = {13, 10};

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(group)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        ByteBuf buf = ctx.alloc().buffer();
                                        buf.writeBytes("*3".getBytes());
                                        buf.writeBytes(LINE);
                                        buf.writeBytes("$3".getBytes());
                                        buf.writeBytes(LINE);
                                        buf.writeBytes("set".getBytes());
                                        buf.writeBytes(LINE);
                                        buf.writeBytes("$4".getBytes());
                                        buf.writeBytes(LINE);
                                        buf.writeBytes("name".getBytes());
                                        buf.writeBytes(LINE);
                                        buf.writeBytes("$6".getBytes());
                                        buf.writeBytes(LINE);
                                        buf.writeBytes("sherry".getBytes());
                                        buf.writeBytes(LINE);
                                        ctx.writeAndFlush(buf);
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(StandardCharsets.UTF_8));
                                    }
                                });
                    }
                })
                .connect(new InetSocketAddress("110.42.187.113", 6379))
                .sync()
                .channel()
                .closeFuture()
                .addListener(future -> group.shutdownGracefully());
    }

}
