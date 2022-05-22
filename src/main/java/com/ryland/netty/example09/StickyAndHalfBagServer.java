package com.ryland.netty.example09;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * ways to solve:
 * 1. short connection: can not handle half bag because the size of client buffer is limited and efficiency is low;
 * 2. fixed length: waste space
 * 3. fixed delimiter: need escape
 * 4. specify the msg length
 *
 * @author Ryland
 */
public class StickyAndHalfBagServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Channel channel = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                // option for global and childOption for each connection
                // tcp sliding window size
                // sticky bag when not configure the option SO_RCVBUF
                //FIXME half bag when configure the option SO_RCVBUF(not take effect,why???)
                // .option(ChannelOption.SO_RCVBUF, 10)
                // RCVBUF_ALLOCATOR works :-)
                // .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(16, 16, 16))
                .group(bossGroup, workerGroup)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG));
                    }
                })
                .bind(8080)
                .sync()
                .channel();
        channel.closeFuture().addListener(future -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });
    }

}
