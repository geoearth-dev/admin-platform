package dev.geo.admin.config.server.model;

import dev.geo.admin.common.utils.Arith;
import lombok.Getter;
import lombok.Setter;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * JVM 信息：保存堆内存字节数，读取容量时转换为 MB（1024 进制）
 *
 * @author qdata
 */
@Getter
@Setter
public class Jvm {

    private static final long BYTES_PER_MB = 1024L * 1024;
    private static final RuntimeMXBean RUNTIME = ManagementFactory.getRuntimeMXBean();
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * JVM 已分配堆内存，字节
     */
    private double total;

    /**
     * JVM 最大堆内存，字节
     */
    private double max;

    /**
     * 已分配堆内存中的空闲部分，字节
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    public double getTotal() {
        return Arith.div(total, BYTES_PER_MB, 2);
    }

    public double getMax() {
        return Arith.div(max, BYTES_PER_MB, 2);
    }

    public double getFree() {
        return Arith.div(free, BYTES_PER_MB, 2);
    }

    public double getUsed() {
        return Arith.div(total - free, BYTES_PER_MB, 2);
    }

    /** 已用堆内存占已分配堆内存的百分比 */
    public double getUsage() {
        return total > 0 ? Arith.round((total - free) * 100 / total, 2) : 0;
    }

    /**
     * 获取JDK名称
     */
    public String getName() {
        return RUNTIME.getVmName();
    }

    /**
     * JDK启动时间
     */
    public String getStartTime() {
        return Instant.ofEpochMilli(RUNTIME.getStartTime())
                .atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMAT);
    }

    /**
     * JDK运行时间
     */
    public String getRunTime() {
        Duration uptime = Duration.ofMillis(RUNTIME.getUptime());
        return String.format("%d天%d小时%d分钟%d秒", uptime.toDays(), uptime.toHoursPart(),
                uptime.toMinutesPart(), uptime.toSecondsPart());
    }

    /**
     * 运行参数
     */
    public String getInputArgs() {
        return RUNTIME.getInputArguments().toString();
    }
}
