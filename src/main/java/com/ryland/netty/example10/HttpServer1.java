package com.ryland.netty.example10;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryland
 */
@Slf4j
public class HttpServer1 {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(boss, worker)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new HttpServerCodec())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // class io.netty.handler.codec.http.DefaultHttpRequest
                                        // and class io.netty.handler.codec.http.LastHttpContent$1
                                        log.debug("[{}]", msg.getClass());
                                        if (msg instanceof HttpRequest) {
                                            log.debug("http request");
                                        } else if (msg instanceof HttpContent) {
                                            log.debug("http content");
                                        }
                                    }
                                });
                    }
                })
                .bind(8080)
                .sync()
                .channel()
                .closeFuture()
                .addListener(future -> {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                });
    }

}
