package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.GroupJoinRequestMessage;
import com.ryland.netty.example11.message.Message;

/**
 * @author Ryland
 */
public class GroupJoinCmdExec extends CmdWriterExecutor {

    @Override
    protected Message doExec(UserCtx userCtx, String command) {
        String[] s = command.split(" ");
        return new GroupJoinRequestMessage(userCtx.getUsername(), s[1]);
    }

    @Override
    protected boolean checkCmd(String command) {
        return true;
    }
}
