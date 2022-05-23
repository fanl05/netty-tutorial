package com.ryland.netty.example11.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Ryland
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuitRequestMessage extends Message {

    private String username;

    public QuitRequestMessage(String username) {
        this.username = username;
    }

    @Override
    public int getMessageType() {
        return QUIT_REQUEST_MESSAGE;
    }
}
