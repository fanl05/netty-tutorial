package com.ryland.netty.example11.client.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ryland
 */
@Data
@Builder
public class UserCtx {

    private String username;

    public static boolean check(UserCtx ctx) {
        return null != ctx && null != ctx.getUsername();
    }

}
