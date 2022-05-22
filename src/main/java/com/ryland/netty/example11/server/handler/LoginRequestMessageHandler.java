package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.LoginRequestMessage;
import com.ryland.netty.example11.message.LoginResponseMessage;
import com.ryland.netty.example11.server.service.UserServiceFactory;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean isSuccess = UserServiceFactory.getUserService().login(username, password);
        LoginResponseMessage message;
        if (isSuccess) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "success");
        } else {
            message = new LoginResponseMessage(false, "failed");
        }
        ctx.writeAndFlush(message);
    }
}
