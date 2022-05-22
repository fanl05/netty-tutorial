package com.ryland.netty.example11.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * group session management
 *
 * @author Ryland
 */
public interface GroupSession {

    /**
     * create a group
     * succeed when not existed, or else return null
     *
     * @param name    group name
     * @param members group members
     * @return return null when failed
     */
    Group createGroup(String name, Set<String> members);

    /**
     * join group
     *
     * @param name   group name
     * @param member member name
     * @return return null when group not exists
     */
    Group joinMember(String name, String member);

    /**
     * remove member from group
     *
     * @param name   group name
     * @param member member name
     * @return return null when group not exists
     */
    Group removeMember(String name, String member);

    /**
     * remove group
     *
     * @param name group name
     * @return return null when group not exists
     */
    Group removeGroup(String name);

    /**
     * get members of group
     *
     * @param name group name
     * @return group members
     */
    Set<String> getMembers(String name);

    /**
     * get online channels of group
     *
     * @param name group name
     * @return channel list
     */
    List<Channel> getMembersChannel(String name);
}
