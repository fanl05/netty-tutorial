package com.ryland.netty.example11.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ryland
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatRequestMessage extends Message {

    private String content;
    private String to;
    private String from;

    public ChatRequestMessage(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    @Override
    public int getMessageType() {
        return CHAT_REQUEST_MESSAGE;
    }
}
