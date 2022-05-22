package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.GroupChatRequestMessage;
import com.ryland.netty.example11.message.Message;

/**
 * @author Ryland
 */
public class GroupSendCmdExec extends CmdWriterExecutor {

    @Override
    protected Message doExec(UserCtx userCtx, String command) {
        String[] s = command.split(" ");
        return new GroupChatRequestMessage(userCtx.getUsername(), s[1], s[2]);
    }

    @Override
    protected boolean checkCmd(String command) {
        return true;
    }
}
