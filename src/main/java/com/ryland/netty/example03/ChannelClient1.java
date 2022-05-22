package com.ryland.netty.example03;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author Ryland
 */
@Slf4j
public class ChannelClient1 {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // async non-blocking method, main thread continue running
                // NioEventLoopGroup connect the server
                .connect(new InetSocketAddress("localhost", 8080));
        // If we don't invoke sync, the Channel has not established yet
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        log.debug("Channel: [{}]", channel);
        channel.writeAndFlush("hello world");
    }

}
