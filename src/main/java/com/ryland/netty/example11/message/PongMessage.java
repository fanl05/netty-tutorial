package com.ryland.netty.example11.message;

public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PONG_MESSAGE;
    }
}
