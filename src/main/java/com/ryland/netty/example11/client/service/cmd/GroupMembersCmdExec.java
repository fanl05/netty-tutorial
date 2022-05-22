package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.GroupMembersRequestMessage;
import com.ryland.netty.example11.message.Message;

/**
 * @author Ryland
 */
public class GroupMembersCmdExec extends CmdWriterExecutor {

    @Override
    protected Message doExec(UserCtx userCtx, String command) {
        String[] s = command.split(" ");
        return new GroupMembersRequestMessage(s[1]);
    }

    @Override
    protected boolean checkCmd(String command) {
        return true;
    }
}
