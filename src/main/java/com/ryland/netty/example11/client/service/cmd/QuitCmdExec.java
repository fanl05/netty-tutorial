package com.ryland.netty.example11.client.service.cmd;

import com.ryland.netty.example11.client.dto.UserCtx;
import com.ryland.netty.example11.message.QuitRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryland
 */
@Slf4j
public class QuitCmdExec implements CommandExecutor {

    @Override
    public void exec(ChannelHandlerContext ctx, UserCtx userCtx, String command) {
        try {
            ctx.writeAndFlush(new QuitRequestMessage(userCtx.getUsername())).sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("exception: [{}]", e.toString());
        }
        ctx.channel().close();
    }
}
