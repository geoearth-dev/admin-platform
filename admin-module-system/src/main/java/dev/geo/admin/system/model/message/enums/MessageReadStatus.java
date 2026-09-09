package dev.geo.admin.system.model.message.enums;

import lombok.Getter;

/**
 * 消息阅读状态。
 */
@Getter
public enum MessageReadStatus {
    UNREAD(0),
    READ(1);

    private final int code;

    MessageReadStatus(int code) {
        this.code = code;
    }
}
