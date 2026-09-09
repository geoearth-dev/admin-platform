package dev.geo.admin.common.exception.user;


import dev.geo.admin.common.exception.base.BaseException;

/**
 * 用户信息异常类
 */
public class UserException extends BaseException {

    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
