package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.LoginRequestMessage;
import com.ryland.netty.example11.message.LoginResponseMessage;
import com.ryland.netty.example11.server.service.UserServiceFactory;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean isPwdValid = UserServiceFactory.getUserService().login(username, password);
        Channel channel = SessionFactory.getSession().getChannel(username);
        LoginResponseMessage message;
        if (isPwdValid) {
            if (Objects.nonNull(channel)) {
                message = new LoginResponseMessage(false, "You have logged in...");
            } else {
                SessionFactory.getSession().bind(ctx.channel(), username);
                message = new LoginResponseMessage(true, "Login SUCCESS and welcome, my friend!!!");
            }
        } else {
            message = new LoginResponseMessage(false, "Invalid username or password...");
        }
        ctx.writeAndFlush(message);
    }
}
