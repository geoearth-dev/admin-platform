package dev.geo.admin.common.exception.user;

/**
 * 用户不存在异常。
 */
public class UserNotExistsException extends UserException
{

    public UserNotExistsException()
    {
        super("user.not.exists", null);
    }
}
