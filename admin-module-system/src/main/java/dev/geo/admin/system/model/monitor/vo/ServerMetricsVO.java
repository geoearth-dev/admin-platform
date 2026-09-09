package dev.geo.admin.system.model.monitor.vo;

/**
 * 服务器运行指标。
 */
public record ServerMetricsVO(
        int availableProcessors,
        double systemCpuLoad,
        long processUptimeMillis,
        long heapUsedBytes,
        long heapMaxBytes,
        long systemMemoryTotalBytes,
        long systemMemoryFreeBytes,
        String javaVersion,
        String osName,
        String osArch
) {
}
