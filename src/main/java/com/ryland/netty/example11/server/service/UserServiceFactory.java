package com.ryland.netty.example11.server.service;

/**
 * @author Ryland
 */
public abstract class UserServiceFactory {

    private static final UserService userService = new UserServiceMemoryImpl();

    private UserServiceFactory() {
    }

    public static UserService getUserService() {
        return userService;
    }
}
