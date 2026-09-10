package dev.geo.admin.common.enums;

/**
 * 操作状态
 *
 */
public enum BusinessStatus {
    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAIL(0);

    private final int code;

    BusinessStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
