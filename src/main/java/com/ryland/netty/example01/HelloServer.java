package com.ryland.netty.example01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryland
 */
@Slf4j
public class HelloServer {

    /**
     * Steps to develop Netty Server and Client
     * 1. server create NioEventLoopGroup
     * 2. server create ChannelInitializer
     * 3. server wait connection
     * 4. client create NioEventLoopGroup
     * 5. client create ChannelInitializer
     * 6. client connect to server
     * 7. server and client init channel at the same time almost
     * 8. client blocked at method sync until connection established and then get channel
     * 9. client write and flush msg
     * 10. StringEncoder convert String to ByteBuf
     * 11. server receive the msg and the msg go through the pipeline: decode to String and logged
     *
     * Things you need to know about EventLoop
     * 1. EventLoop is the worker processing the data
     * 2. EventLoop and Channel is bound
     * 3. EventLoop can handle tasks(common and timing) except io and each EventLoop has a task queue
     * 4. EventLoop handle data by order defined in initChannel
     * 5. EventLoop can assign different workers for different handlers
     */
    public static void main(String[] args) {
        new ServerBootstrap()
                // NioEventLoopGroup contains Selector and Thread
                .group(new NioEventLoopGroup())
                // implement of ServerSocketChannel
                .channel(NioServerSocketChannel.class)
                // determine what the worker will do
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                // decode: ByteBuf -> String
                                .addLast(new StringDecoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("received: [{}]", msg);
                                    }
                                });
                    }
                })
                .bind(8080);
    }

}
