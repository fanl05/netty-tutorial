package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.QuitNoticeMessage;
import com.ryland.netty.example11.message.QuitRequestMessage;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class QuitRequestMessageHandler extends SimpleChannelInboundHandler<QuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitRequestMessage msg) throws Exception {
        SessionFactory
                .getSession()
                .getAllChannels()
                .forEach(ch -> ch.writeAndFlush(new QuitNoticeMessage(true, msg.getUsername() + " is offline")));
    }
}
