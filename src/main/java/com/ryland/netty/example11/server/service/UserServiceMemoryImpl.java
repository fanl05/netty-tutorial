package com.ryland.netty.example11.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ryland
 */
public class UserServiceMemoryImpl implements UserService {

    private final Map<String, String> users = new ConcurrentHashMap<>();

    public UserServiceMemoryImpl() {
        users.put("sherry", "123");
        users.put("ryland", "123");
        users.put("fan", "123");
        users.put("li", "123");
    }

    @Override
    public boolean login(String username, String password) {
        String pass = users.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
