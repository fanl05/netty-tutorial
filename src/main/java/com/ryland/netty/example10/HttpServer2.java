package com.ryland.netty.example10;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * @author Ryland
 */
@Slf4j
public class HttpServer2 {

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
                                // both inbound and outbound
                                .addLast(new HttpServerCodec())
                                // SimpleChannelInboundHandler only handle the specific type
                                .addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                        log.debug(msg.uri());
                                        DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                        byte[] bytes = "<h1>Hello Wolrd...</h1>".getBytes(StandardCharsets.UTF_8);
                                        response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                        response.content().writeBytes(bytes);

                                        ctx.writeAndFlush(response);
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
