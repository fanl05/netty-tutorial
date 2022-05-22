package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.ChatRequestMessage;
import com.ryland.netty.example11.message.ChatResponseMessage;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        // online
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        // offline
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, "unknown or offline target"));
        }
    }
}
