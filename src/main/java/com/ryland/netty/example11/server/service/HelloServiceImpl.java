package com.ryland.netty.example11.server.service;

/**
 * @author Ryland
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String msg) {
        // int i = 1 / 0;
        return "Hello " + msg;
    }
}