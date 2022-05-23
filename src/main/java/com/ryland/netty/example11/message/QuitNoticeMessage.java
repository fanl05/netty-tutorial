package com.ryland.netty.example11.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Ryland
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuitNoticeMessage extends AbstractResponseMessage {

    public QuitNoticeMessage(boolean success, String comment) {
        super(success, comment);
    }

    @Override
    public int getMessageType() {
        return QUIT_NOTICE_MESSAGE;
    }
}
