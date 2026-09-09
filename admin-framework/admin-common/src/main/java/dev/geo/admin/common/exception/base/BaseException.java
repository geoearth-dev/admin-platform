package dev.geo.admin.common.exception.base;

import lombok.Getter;

/**
 * Basic exception
 *
 */
@Getter
public class BaseException extends RuntimeException {
    /**
     * 所属模块
     */
    private final String module;

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误码对应的参数
     */
    private final Object[] args;

    /**
     * 错误消息
     */
    private final String defaultMessage;

    public BaseException(String module, String code, Object[] args, String defaultMessage) {
        super(code);
        this.module = module;
        this.code = code;
        this.args = args;
        this.defaultMessage = defaultMessage;
    }

}
