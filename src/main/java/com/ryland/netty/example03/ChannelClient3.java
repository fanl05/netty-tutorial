package com.ryland.netty.example03;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author Ryland
 */
@Slf4j
public class ChannelClient3 {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));

        Channel channel = channelFuture.sync().channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if ("q".equals(line)) {
                    // close is also a async method
                    // so the real close of Channel is not necessarily before logging
                    channel.close();
                    log.debug("after close");
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "input").start();

        // use CloseFuture to do some things after Channel close
        ChannelFuture closeFuture = channel.closeFuture();
        // 1. sync version
        // log.debug("waiting close...");
        // closeFuture.sync();
        // log.debug("do some things after Channel close");

        // 2. async version
        closeFuture.addListener((ChannelFutureListener) future -> {
            log.debug("use ChannelFutureListener to do some things after Channel close");
            log.debug("[{}]", Thread.currentThread().getName());
        });

        // Q: Why java process do not terminate???
        // A: Some threads in NioSocketChannel is still running
        // So we should do something to shut down the process gracefully
    }

}
