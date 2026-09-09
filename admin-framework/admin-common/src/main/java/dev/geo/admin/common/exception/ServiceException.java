package dev.geo.admin.common.exception;


import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public final class ServiceException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * i18n message key (corresponding to the key in messages.properties)
     */
    private String i18nCode;

    /**
     * Message formatting parameters
     */
    private Object[] args;

    /**
     * 错误提示
     */
    private final String message;


    /**
     * Use plain text message construction (no i18n)
     */
    public ServiceException(String message) {
        this.message = message;
    }

    /**
     * Constructed using plain text message + HTTP status code
     */
    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    /**
     * Use i18n message key + hidden message structure
     * Prioritize getting the copy in the current language from the resource file. If it cannot be obtained, use defaultMessage.
     *
     * @param i18nCode       message key (such as "user.not.exists")
     * @param defaultMessage divulge message
     * @param args           format parameters (can replace {0}, {1} and other placeholders)
     */
    public ServiceException(String i18nCode, String defaultMessage, Object... args) {
        this.i18nCode = i18nCode;
        this.message = defaultMessage;
        this.args = args;
    }

    /**
     * Use i18n message key + cryptic message + HTTP status code structure
     */
    public ServiceException(String i18nCode, String defaultMessage, Integer code, Object... args) {
        this.i18nCode = i18nCode;
        this.message = defaultMessage;
        this.code = code;
        this.args = args;
    }
}
