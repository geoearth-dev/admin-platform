package dev.geo.admin.config.server.model;

import dev.geo.admin.common.utils.Arith;
import lombok.Getter;
import lombok.Setter;

/**
 * CPU相关信息
 *
 */
@Getter
@Setter
public class Cpu {

    /**
     * 逻辑处理器数量
     */
    private int cpuNum;

    /**
     * 采样期间的总时钟增量（getter 返回总使用率，不含空闲和 I/O 等待）
     */
    private double total;

    /**
     * 系统态时钟增量
     */
    private double sys;

    /**
     * 用户态时钟增量
     */
    private double used;

    /**
     * I/O 等待时钟增量
     */
    private double wait;

    /**
     * 空闲时钟增量
     */
    private double free;

    public double getTotal() {
        return percentage(total - free - wait);
    }

    public double getSys() {
        return percentage(sys);
    }

    public double getUsed() {
        return percentage(used);
    }

    public double getWait() {
        return percentage(wait);
    }

    public double getFree() {
        return percentage(free);
    }

    private double percentage(double ticks) {
        return total > 0 ? Arith.round(ticks * 100 / total, 2) : 0;
    }
}
