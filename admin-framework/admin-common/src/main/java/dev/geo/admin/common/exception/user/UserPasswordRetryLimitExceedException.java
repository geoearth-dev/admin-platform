package dev.geo.admin.common.exception.user;

/**
 * 用户错误最大次数异常类
 */
public class UserPasswordRetryLimitExceedException extends UserException {
    public UserPasswordRetryLimitExceedException(int retryLimitCount, int lockTime) {
        super("user.password.retry.limit.exceed", new Object[]{retryLimitCount, lockTime});
    }
}
