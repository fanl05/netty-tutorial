package com.ryland.netty.example11.client.service.cmd;

import static com.ryland.netty.example11.common.Constants.SPACE;

/**
 * @author Ryland
 */
public class CmdUtils {

    private CmdUtils() {
    }

    public static String getCmdType(String command) {
        if (null == command) {
            return null;
        }
        return command.split(SPACE)[0];
    }

}
