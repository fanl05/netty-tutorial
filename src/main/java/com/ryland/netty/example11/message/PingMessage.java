package com.ryland.netty.example11.message;

/**
 * @author Ryland
 */
public class PingMessage extends Message {

    @Override
    public int getMessageType() {
        return PING_MESSAGE;
    }
}
