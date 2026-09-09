package dev.geo.admin.common.exception.user;

/**
 * 黑名单IP异常类
 */
public class BlackListException extends UserException {
    public BlackListException() {
        super("login.blocked", null);
    }
}
