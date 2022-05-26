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
public class RpcResponseMessage extends Message {

    /**
     * return value
     */
    private Object returnValue;

    /**
     * exception value
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
