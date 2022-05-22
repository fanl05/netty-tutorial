package com.ryland.netty.example11.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class GroupJoinResponseMessage extends AbstractResponseMessage {

    public GroupJoinResponseMessage(boolean success, String reason) {
        super(success, reason);
    }

    @Override
    public int getMessageType() {
        return GROUP_JOIN_RESPONSE_MESSAGE;
    }
}
