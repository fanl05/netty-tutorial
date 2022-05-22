package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.GroupChatRequestMessage;
import com.ryland.netty.example11.message.GroupChatResponseMessage;
import com.ryland.netty.example11.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {

        GroupSessionFactory
                .getGroupSession()
                .getMembersChannel(msg.getGroupName())
                .forEach(ch -> ch.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent(), msg.getGroupName())));

    }
}
