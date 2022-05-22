package com.ryland.netty.example11.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * @author Ryland
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupCreateRequestMessage extends Message {

    private String groupName;
    private Set<String> members;

    public GroupCreateRequestMessage(String groupName, Set<String> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public int getMessageType() {
        return GROUP_CREATE_REQUEST_MESSAGE;
    }
}
