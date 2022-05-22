package com.ryland.netty.example02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author Ryland
 */
@Slf4j
public class EventLoopServer {

    public static void main(String[] args) {
        // create a EventLoopGroup to handle the time-cost step
        EventLoopGroup group = new DefaultEventLoopGroup();

        new ServerBootstrap()
                // boss take in charge of 'accept'
                // worker take in charge of 'read'
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("first-handler", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.defaultCharset()));
                                        // pass the msg on the next Handler
                                        ctx.fireChannelRead(msg);
                                    }
                                })
                                // specify the EventLoopGroup the Handler uses
                                // DefaultEventLoopGroup will also bind the Channel
                                // see AbstractChannelHandlerContext#invokeChannelRead to know how thread changed
                                .addLast(group, "second-handler", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.defaultCharset()));
                                        ctx.fireChannelRead(msg);
                                    }
                                })
                                .addLast("third-handler", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.defaultCharset()));
                                    }
                                });
                    }
                })
                .bind(8080);
    }

}
