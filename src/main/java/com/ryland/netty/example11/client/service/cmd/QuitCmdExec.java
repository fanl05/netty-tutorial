package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Ryland
 */
public class QuitCmdExec implements CommandExecutor {

    @Override
    public void exec(ChannelHandlerContext ctx, UserCtx userCtx, String command) {
        ctx.channel().close();
    }
}
