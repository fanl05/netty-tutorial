package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryland
 */
@Slf4j
public abstract class CmdWriterExecutor implements CommandExecutor {

    @Override
    public void exec(ChannelHandlerContext ctx, UserCtx userCtx, String command) {
        boolean isValid = UserCtx.check(userCtx);
        if (!isValid) {
            log.error("user context check failed");
            return;
        }
        isValid = checkCmd(command);
        if (!isValid) {
            log.error("unknown command");
            return;
        }
        Message message = doExec(userCtx, command);
        if (null != message) {
            ctx.writeAndFlush(message);
        }
    }

    protected abstract Message doExec(UserCtx userCtx, String command);

    protected abstract boolean checkCmd(String command);
}
