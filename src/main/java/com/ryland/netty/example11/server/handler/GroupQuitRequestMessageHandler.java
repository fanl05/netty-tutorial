package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.GroupQuitRequestMessage;
import com.ryland.netty.example11.message.GroupQuitResponseMessage;
import com.ryland.netty.example11.server.session.Group;
import com.ryland.netty.example11.server.session.GroupSessionFactory;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {

        Group group = GroupSessionFactory.getGroupSession().removeMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            group.getMembers().forEach(username -> SessionFactory
                    .getSession()
                    .getChannel(username)
                    .writeAndFlush(new GroupQuitResponseMessage(true, msg.getUsername() + " exit " + msg.getGroupName())));
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, "exit " + msg.getGroupName()));
        } else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, msg.getGroupName() + " is not existed"));
        }
    }
}
