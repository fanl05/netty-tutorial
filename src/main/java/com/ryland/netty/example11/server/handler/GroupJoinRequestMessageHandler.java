package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.GroupJoinRequestMessage;
import com.ryland.netty.example11.message.GroupJoinResponseMessage;
import com.ryland.netty.example11.server.session.Group;
import com.ryland.netty.example11.server.session.GroupSessionFactory;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {

        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            GroupSessionFactory
                    .getGroupSession()
                    .getMembersChannel(msg.getGroupName())
                    .stream()
                    .filter(ch -> !Objects.equals(msg.getUsername(), SessionFactory.getSession().getUsername(ch)))
                    .forEach(ch -> ch.writeAndFlush(new GroupJoinRequestMessage(msg.getUsername(), msg.getGroupName())));
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, "Succeed in joining" + msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + " fail to join"));
        }
    }
}
