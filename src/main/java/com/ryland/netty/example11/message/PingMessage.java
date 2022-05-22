package com.ryland.netty.example11.message;

public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PING_MESSAGE;
    }
}
