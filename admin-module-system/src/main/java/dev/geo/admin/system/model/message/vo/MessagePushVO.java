package dev.geo.admin.system.model.message.vo;

/**
 * WebSocket 消息推送载荷。
 */
public record MessagePushVO(String type, long unreadCount) {
    public static MessagePushVO unreadCount(long count) {
        return new MessagePushVO("unread-count", count);
    }
}
