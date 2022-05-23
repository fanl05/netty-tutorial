package com.ryland.netty.example11.client;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.client.service.cmd.CmdUtils;
import com.ryland.netty.example11.client.service.cmd.CommandExecutor;
import com.ryland.netty.example11.client.service.cmd.CommandFactory;
import com.ryland.netty.example11.client.utils.Utils;
import com.ryland.netty.example11.message.*;
import com.ryland.netty.example11.protocol.MessageCodecSharable;
import com.ryland.netty.example11.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ryland.netty.example11.client.service.cmd.Command.QUIT;

/**
 * @author Ryland
 */
@Slf4j
public class ChatClient {

    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.ERROR);
    private static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
    private static final CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
    private static final AtomicBoolean LOGIN = new AtomicBoolean(false);
    private static final AtomicBoolean EXIT = new AtomicBoolean(false);

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Scanner scanner = new Scanner(System.in);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new ProcotolFrameDecoder())
                            .addLast(LOGGING_HANDLER)
                            .addLast(MESSAGE_CODEC)
                            .addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS))
                            .addLast(new ChannelDuplexHandler() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    if (IdleState.WRITER_IDLE == event.state()) {
                                        ctx.writeAndFlush(new PingMessage());
                                        log.debug("heart beat sent");
                                    }
                                }
                            })
                            .addLast("client handler", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("msg: [{}]", msg);
                                    if ((msg instanceof LoginResponseMessage)) {
                                        LoginResponseMessage response = (LoginResponseMessage) msg;
                                        if (response.isSuccess()) {
                                            LOGIN.set(true);
                                        }
                                        System.out.println(((LoginResponseMessage) msg).getComment());
                                        WAIT_FOR_LOGIN.countDown();
                                    } else if (msg instanceof ChatResponseMessage) {
                                        ChatResponseMessage chatMsg = (ChatResponseMessage) msg;
                                        System.out.println("=======" + chatMsg.getFrom() + " msg=======");
                                        System.out.println("msg: " + chatMsg.getContent());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof GroupCreateResponseMessage) {
                                        GroupCreateResponseMessage gcreateMsg = (GroupCreateResponseMessage) msg;
                                        System.out.println("=======group create======");
                                        System.out.println(gcreateMsg.getComment());
                                        System.out.println("creator: " + gcreateMsg.getCreator());
                                        System.out.println("current members: " + gcreateMsg.getMembers());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof GroupChatResponseMessage) {
                                        GroupChatResponseMessage gchatMsg = (GroupChatResponseMessage) msg;
                                        System.out.println("========group msg========");
                                        System.out.println("group: " + gchatMsg.getGroupName());
                                        System.out.println("from: " + gchatMsg.getFrom());
                                        System.out.println("msg: " + gchatMsg.getContent());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof GroupMembersResponseMessage) {
                                        GroupMembersResponseMessage gmemberMsg = (GroupMembersResponseMessage) msg;
                                        System.out.println("=======group members=======");
                                        System.out.println(gmemberMsg.getMembers());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof GroupJoinRequestMessage) {
                                        GroupJoinRequestMessage gjoinReqMsg = (GroupJoinRequestMessage) msg;
                                        System.out.println("======group member join======");
                                        System.out.println("group: " + gjoinReqMsg.getGroupName());
                                        System.out.println("username: " + gjoinReqMsg.getUsername());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof GroupJoinResponseMessage) {
                                        GroupJoinResponseMessage gjoinRespMsg = (GroupJoinResponseMessage) msg;
                                        System.out.println("======group member join======");
                                        System.out.println(gjoinRespMsg.getComment());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof GroupQuitResponseMessage) {
                                        GroupQuitResponseMessage gquitRespMsg = (GroupQuitResponseMessage) msg;
                                        System.out.println("======group member quit======");
                                        System.out.println(gquitRespMsg.getComment());
                                        Utils.consolePrintSeparator();
                                    } else if (msg instanceof QuitNoticeMessage) {
                                        Utils.consolePrintSeparator();
                                        System.out.println(((QuitNoticeMessage) msg).getComment());
                                        Utils.consolePrintSeparator();
                                    }
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // receive the console input and send to server
                                    group.submit(() -> {
                                        System.out.println("Please input username:");
                                        String username = scanner.nextLine();
                                        if (EXIT.get()) {
                                            return;
                                        }
                                        System.out.println("Please input password:");
                                        String password = scanner.nextLine();
                                        if (EXIT.get()) {
                                            return;
                                        }
                                        LoginRequestMessage message = new LoginRequestMessage(username, password);
                                        ctx.writeAndFlush(message);
                                        System.out.println("Waiting following operation...");
                                        try {
                                            WAIT_FOR_LOGIN.await();
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                            log.debug("[{}]", e.toString());
                                        }
                                        if (!LOGIN.get()) {
                                            ctx.channel().close();
                                            return;
                                        }
                                        while (true) {
                                            Utils.consolePrintMenu();
                                            String command;
                                            try {
                                                command = scanner.nextLine();
                                            } catch (Exception e) {
                                                break;
                                            }
                                            if (EXIT.get()) {
                                                return;
                                            }
                                            String cmdType = CmdUtils.getCmdType(command);
                                            CommandExecutor executor = CommandFactory.getExecutorByCmdType(cmdType);
                                            executor.exec(ctx, UserCtx.builder().username(username).build(), command);
                                            if (QUIT.equals(cmdType)) {
                                                return;
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("The connection is disconnected, press any key to exit...");
                                    EXIT.set(true);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    log.error("exception occurs: [{}]", cause.toString());
                                    log.debug("The connection is disconnected, press any key to exit...");
                                    EXIT.set(true);
                                }
                            });
                }
            });
            Channel channel = bootstrap.connect("localhost", 9090).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.error("Exception occurs: [{}]", e.toString());
        } finally {
            group.shutdownGracefully();
        }
    }
}
