package com.ryland.netty.example11.client.service.cmd;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryland
 */
public class CommandFactory {

    private static final Map<String, CommandExecutor> COMMAND_EXECUTOR_MAP;

    private static final CommandExecutor DEFAULT_EXECUTOR = new DefaultCmdExecutor();

    static {
        COMMAND_EXECUTOR_MAP = new HashMap<>();
        COMMAND_EXECUTOR_MAP.put(Command.SEND, new SendCmdExec());
        COMMAND_EXECUTOR_MAP.put(Command.GROUP_SEND, new GroupSendCmdExec());
        COMMAND_EXECUTOR_MAP.put(Command.GROUP_CREATE, new GroupCreateCmdExec());
        COMMAND_EXECUTOR_MAP.put(Command.GROUP_MEMBERS, new GroupMembersCmdExec());
        COMMAND_EXECUTOR_MAP.put(Command.GROUP_JOIN, new GroupJoinCmdExec());
        COMMAND_EXECUTOR_MAP.put(Command.GROUP_QUIT, new GroupQuitCmdExec());
        COMMAND_EXECUTOR_MAP.put(Command.QUIT, new QuitCmdExec());
    }

    private CommandFactory() {
    }

    public static CommandExecutor getExecutorByCmdType(String type) {
        return COMMAND_EXECUTOR_MAP.getOrDefault(type, DEFAULT_EXECUTOR);
    }

}
