package dev.geo.admin.common.constant;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 系统统一时间格式。
 */
public final class DateTimeFormat {
    private DateTimeFormat() {
    }

    public static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String COMPACT_DATE_TIME_PATTERN = "yyyyMMddHHmmss";

    public static final String DATE_PATH_PATTERN = "yyyy/MM/dd";

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final DateTimeFormatter COMPACT_DATE_TIME = DateTimeFormatter.ofPattern(COMPACT_DATE_TIME_PATTERN).withZone(ZoneOffset.UTC);


    public static final DateTimeFormatter DATE_PATH = DateTimeFormatter.ofPattern(DATE_PATH_PATTERN).withZone(ZoneOffset.UTC);

}
