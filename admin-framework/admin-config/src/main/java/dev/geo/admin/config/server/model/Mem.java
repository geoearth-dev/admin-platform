package dev.geo.admin.config.server.model;

import dev.geo.admin.common.utils.Arith;
import lombok.Setter;

/**
 * 内存信息：保存字节数，读取容量时转换为 GB（1024 进制）
 *
 */
@Setter
public class Mem {

    private static final long BYTES_PER_GB = 1024L * 1024 * 1024;

    /**
     * 内存总量
     */
    private long total;

    /**
     * 已用内存
     */
    private long used;

    /**
     * 剩余内存
     */
    private long free;

    public double getTotal() {
        return Arith.div(total, BYTES_PER_GB, 2);
    }

    public double getUsed() {
        return Arith.div(used, BYTES_PER_GB, 2);
    }

    public double getFree() {
        return Arith.div(free, BYTES_PER_GB, 2);
    }

    public double getUsage() {
        return total > 0 ? Arith.round(used * 100.0 / total, 2) : 0;
    }
}
