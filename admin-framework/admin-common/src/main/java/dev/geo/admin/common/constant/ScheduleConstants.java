package dev.geo.admin.common.constant;

import lombok.Getter;

/**
 * 任务调度通用常量
 */
public class ScheduleConstants {
    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /**
     * 执行目标key
     */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /**
     * 1 表示允许并发，0 表示禁止并发。
     */
    public static final String CONCURRENT_ALLOW = "1";
    public static final String CONCURRENT_DISALLOW = "0";

    /**
     * 1 表示执行成功，0 表示执行失败。
     */
    public static final String EXECUTION_SUCCESS = "1";
    public static final String EXECUTION_FAIL = "0";

    /**
     * 默认
     */
    public static final String MISFIRE_DEFAULT = "0";

    /**
     * 立即触发执行
     */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /**
     * 触发一次执行
     */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /**
     * 不触发立即执行
     */
    public static final String MISFIRE_DO_NOTHING = "3";

    @Getter
    public enum Status {
        /**
         * 正常
         */
        NORMAL("1"),
        /**
         * 暂停
         */
        PAUSE("0");

        private final String value;

        private Status(String value) {
            this.value = value;
        }

    }
}
