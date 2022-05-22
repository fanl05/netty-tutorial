package com.ryland.netty.example11.server.session;


import io.netty.channel.Channel;

/**
 * session management
 *
 * @author Ryland
 */
public interface Session {

    /**
     * bind session
     *
     * @param channel  which channel to bind
     * @param username user who binds
     */
    void bind(Channel channel, String username);

    /**
     * unbind session
     *
     * @param channel which channel to unbind
     */
    void unbind(Channel channel);

    /**
     * get attribute
     *
     * @param channel which channel to get
     * @param name    attribute name
     * @return attribute value
     */
    Object getAttribute(Channel channel, String name);

    /**
     * set attribute
     *
     * @param channel which channel to set
     * @param name    attribute name
     * @param value   attribute value
     */
    void setAttribute(Channel channel, String name, Object value);

    /**
     * get channel by username
     *
     * @param username username
     * @return channel
     */
    Channel getChannel(String username);


    /**
     * get username by Channel
     *
     * @param channel channel
     * @return username
     */
    String getUsername(Channel channel);
}
