package dev.geo.admin.system.controller.monitor;

import com.sun.management.OperatingSystemMXBean;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.system.model.monitor.vo.ServerMetricsVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * 服务器运行状态接口。
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {
    @PreAuthorize("@se.hasPermission('monitor:server:list')")
    @GetMapping
    public ApiResult<ServerMetricsVO> getInfo() {
        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        return ApiResult.success(new ServerMetricsVO(
                os.getAvailableProcessors(), os.getCpuLoad(), runtime.getUptime(),
                memory.getHeapMemoryUsage().getUsed(), memory.getHeapMemoryUsage().getMax(),
                os.getTotalMemorySize(), os.getFreeMemorySize(),
                System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch")
        ));
    }
}
