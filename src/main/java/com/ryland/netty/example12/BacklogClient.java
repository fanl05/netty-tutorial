package com.ryland.netty.example12;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * the third client is unable to connect
 * because server SO_BACKLOG is set to 2
 *
 * @author Ryland
 */
@Slf4j
public class BacklogClient {

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(worker)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler());
                        }
                    })
                    .connect("localhost", 8080)
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException ex) {
            log.error(ex.toString());
            Thread.currentThread().interrupt();
        } finally {
            worker.shutdownGracefully();
        }
    }

}
