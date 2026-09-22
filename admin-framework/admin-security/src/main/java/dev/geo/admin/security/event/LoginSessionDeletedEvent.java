package dev.geo.admin.security.event;

/** forced 用于区分管理员强退和普通会话失效提示。 */
public record LoginSessionDeletedEvent(String sessionId, boolean forced) {
}
