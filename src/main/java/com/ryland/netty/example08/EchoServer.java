package com.ryland.netty.example08;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * Note:
 * both NIO and Java Socket are full duplex
 * read and write are not blocked from each other
 *
 * @author Ryland
 */
@Slf4j
public class EchoServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Channel channel = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        String str = buf.toString(StandardCharsets.UTF_8);
                                        log.debug("received from: [{}], msg: [{}]", ch, str);
                                        // write ByteBuf
                                        // ch.writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes(str.getBytes(StandardCharsets.UTF_8)));
                                        // both OK, but this way is recommended
                                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes(str.getBytes(StandardCharsets.UTF_8)));
                                        super.channelRead(ctx, msg);
                                    }
                                });
                    }
                })
                .bind(8080)
                .channel();
        channel.closeFuture().addListener(future -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        });

    }

}
