package com.ryland.netty.example11.server;

import com.ryland.netty.example11.protocol.MessageCodecSharable;
import com.ryland.netty.example11.protocol.ProcotolFrameDecoder;
import com.ryland.netty.example11.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryland
 */
@Slf4j
public class ChatServer {

    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.WARN);
    private static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
    private static final LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
    private static final ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
    private static final GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
    private static final GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
    private static final GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
    private static final GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
    private static final GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
    private static final QuitHandler QUIT_HANDLER = new QuitHandler();

    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProcotolFrameDecoder());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                            // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
//                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            // ChannelDuplexHandler 可以同时作为入站和出站处理器
//                            ch.pipeline().addLast(new ChannelDuplexHandler() {
//                                // 用来触发特殊事件
//                                @Override
//                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//                                    IdleStateEvent event = (IdleStateEvent) evt;
//                                    // 触发了读空闲事件
//                                    if (event.state() == IdleState.READER_IDLE) {
//                                        log.debug("已经 5s 没有读到数据了");
//                                        ctx.channel().close();
//                                    }
//                                }
//                            });
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            ch.pipeline().addLast(CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                            ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                            ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                            ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                            ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                            ch.pipeline().addLast(QUIT_HANDLER);
                        }
                    })
                    .bind(9090)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
            Thread.currentThread().interrupt();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}