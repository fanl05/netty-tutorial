package com.ryland.netty.example11.server.handler;

import com.ryland.netty.example11.message.GroupCreateRequestMessage;
import com.ryland.netty.example11.message.GroupCreateResponseMessage;
import com.ryland.netty.example11.server.session.Group;
import com.ryland.netty.example11.server.session.GroupSession;
import com.ryland.netty.example11.server.session.GroupSessionFactory;
import com.ryland.netty.example11.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Ryland
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();

        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            String username = SessionFactory.getSession().getUsername(ctx.channel());
            channels.forEach(ch -> ch.writeAndFlush(new GroupCreateResponseMessage(
                    true,
                    "You are into " + groupName,
                    username,
                    new ArrayList<>(members))));
        } else {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + " has already been created!!!"));
        }
    }
}
