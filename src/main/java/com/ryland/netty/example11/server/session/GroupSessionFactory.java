package com.ryland.netty.example11.server.session;

/**
 * @author Ryland
 */
public abstract class GroupSessionFactory {

    private static final GroupSession SESSION = new GroupSessionMemoryImpl();

    private GroupSessionFactory() {
    }

    public static GroupSession getGroupSession() {
        return SESSION;
    }
}
