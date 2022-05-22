package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.GroupCreateRequestMessage;
import com.ryland.netty.example11.message.Message;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.ryland.netty.example11.common.Constants.COMMA;
import static com.ryland.netty.example11.common.Constants.SPACE;

/**
 * @author Ryland
 */
public class GroupCreateCmdExec extends CmdWriterExecutor {

    @Override
    protected Message doExec(UserCtx userCtx, String command) {
        String[] s = command.split(SPACE);
        Set<String> set = new HashSet<>(Arrays.asList(s[2].split(COMMA)));
        set.add(userCtx.getUsername());
        return new GroupCreateRequestMessage(s[1], set);
    }

    @Override
    protected boolean checkCmd(String command) {
        return true;
    }
}
