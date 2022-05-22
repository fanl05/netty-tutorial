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
public abstract class AbstractResponseMessage extends Message {

    private boolean success;
    private String comment;

    protected AbstractResponseMessage() {
    }

    protected AbstractResponseMessage(boolean success, String comment) {
        this.success = success;
        this.comment = comment;
    }
}
