package dev.geo.admin.system.model.message.event;

/**
 * 未读消息数量变化事件。
 */
public record MessageUnreadChangedEvent(Long receiverId, long unreadCount) {
}
