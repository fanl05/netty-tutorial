package com.ryland.netty.example11.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Ryland
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RpcRequestMessage extends Message {

    /**
     * reference name
     */
    private final String interfaceName;
    /**
     * method name
     */
    private final String methodName;
    /**
     * return type
     */
    private final Class<?> returnType;
    /**
     * parameter types array
     */
    private final Class<?>[] parameterTypes;
    /**
     * parameter values array
     */
    private final Object[] parameterValue;

    public RpcRequestMessage(int sequenceId, String interfaceName, String methodName, Class<?> returnType, Class<?>[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
