package com.ryland.netty.example11.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author Ryland
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GroupCreateResponseMessage extends AbstractResponseMessage {

    private String creator;
    private List<String> members;

    public GroupCreateResponseMessage(boolean success, String content) {
        super(success, content);
    }

    public GroupCreateResponseMessage(boolean success, String content, String creator, List<String> members) {
        super(success, content);
        this.creator = creator;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GROUP_CREATE_RESPONSE_MESSAGE;
    }
}
