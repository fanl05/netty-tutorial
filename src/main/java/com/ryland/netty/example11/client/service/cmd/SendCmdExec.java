package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.ChatRequestMessage;
import com.ryland.netty.example11.message.Message;

/**
 * @author Ryland
 */
public class SendCmdExec extends CmdWriterExecutor {

    @Override
    protected Message doExec(UserCtx userCtx, String command) {
        String[] s = command.split(" ");
        return new ChatRequestMessage(userCtx.getUsername(), s[1], s[2]);
    }

    @Override
    protected boolean checkCmd(String command) {
        if (null == command) {
            return false;
        }
        String[] s = command.split(" ");
        return s.length == 3;
    }
}
