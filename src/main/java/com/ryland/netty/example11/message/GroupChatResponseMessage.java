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
public class GroupChatResponseMessage extends AbstractResponseMessage {

    private String from;
    private String content;
    private String groupName;

    public GroupChatResponseMessage(String from, String content, String groupName) {
        this.from = from;
        this.content = content;
        this.groupName = groupName;
    }

    @Override
    public int getMessageType() {
        return GROUP_CHAT_RESPONSE_MESSAGE;
    }
}
