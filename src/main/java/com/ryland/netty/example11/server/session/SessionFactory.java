package com.ryland.netty.example11.server.session;

/**
 * @author Ryland
 */
public abstract class SessionFactory {

    private static final Session SESSION = new SessionMemoryImpl();

    private SessionFactory() {
    }

    public static Session getSession() {
        return SESSION;
    }
}
