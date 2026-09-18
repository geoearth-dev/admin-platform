package dev.geo.admin.config.server;

import dev.geo.admin.common.utils.Arith;
import dev.geo.admin.common.utils.ip.IpUtils;
import dev.geo.admin.config.server.model.*;
import lombok.Getter;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 服务器监控快照。每次请求创建一个实例，再调用 copyTo() 采集数据。
 */
@Getter
public class Server {

    private static final long CPU_SAMPLE_MILLIS = 1000;

    /**
     * CPU 信息
     */
    private final Cpu cpu = new Cpu();

    /**
     * 物理内存信息
     */
    private final Mem mem = new Mem();

    /**
     * JVM 信息
     */
    private final Jvm jvm = new Jvm();

    /**
     * 系统信息
     */
    private final Sys sys = new Sys();

    /**
     * 磁盘信息
     */
    private List<SysFile> sysFiles = new ArrayList<>();

    /**
     * CPU 使用率需要两次采样，间隔至少一秒；等待期间先采集其他信息。
     */
    public void copyTo() throws InterruptedException {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        long[] previousTicks = processor.getSystemCpuLoadTicks();
        long sampleStart = System.nanoTime();

        setMemInfo(hardware.getMemory());
        setSysInfo();
        setJvmInfo();
        setSysFiles(systemInfo.getOperatingSystem());

        long remaining = TimeUnit.MILLISECONDS.toNanos(CPU_SAMPLE_MILLIS)
                - (System.nanoTime() - sampleStart);
        if (remaining > 0) {
            TimeUnit.NANOSECONDS.sleep(remaining);
        }
        setCpuInfo(processor, previousTicks);
    }

    private void setCpuInfo(CentralProcessor processor, long[] previousTicks) {
        long[] ticks = processor.getSystemCpuLoadTicks().clone();
        long total = 0;
        for (TickType type : TickType.values()) {
            int index = type.getIndex();
            ticks[index] = Math.max(0, ticks[index] - previousTicks[index]);
            total += ticks[index];
        }
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(total);
        cpu.setSys(ticks[TickType.SYSTEM.getIndex()]);
        cpu.setUsed(ticks[TickType.USER.getIndex()]);
        cpu.setWait(ticks[TickType.IOWAIT.getIndex()]);
        cpu.setFree(ticks[TickType.IDLE.getIndex()]);
    }

    private void setMemInfo(GlobalMemory memory) {
        long total = memory.getTotal();
        long available = memory.getAvailable();
        mem.setTotal(total);
        mem.setUsed(total - available);
        mem.setFree(available);
    }

    private void setSysInfo() {
        sys.setComputerName(IpUtils.getHostName());
        sys.setComputerIp(IpUtils.getHostIp());
        sys.setOsName(System.getProperty("os.name"));
        sys.setOsArch(System.getProperty("os.arch"));
        sys.setUserDir(System.getProperty("user.dir"));
    }

    private void setJvmInfo() {
        // 一次读取堆快照，避免多次读取时 GC 改变内存数据。
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        jvm.setTotal(heap.getCommitted());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(heap.getCommitted() - heap.getUsed());
        jvm.setVersion(System.getProperty("java.version"));
        jvm.setHome(System.getProperty("java.home"));
    }

    private void setSysFiles(OperatingSystem os) {
        List<OSFileStore> stores = os.getFileSystem().getFileStores();
        List<SysFile> files = new ArrayList<>(stores.size());
        for (OSFileStore store : stores) {
            long total = store.getTotalSpace();
            long free = store.getUsableSpace();
            long used = total - free;

            SysFile file = new SysFile();
            file.setDirName(store.getMount());
            file.setSysTypeName(store.getType());
            file.setTypeName(store.getName());
            file.setTotal(convertFileSize(total));
            file.setFree(convertFileSize(free));
            file.setUsed(convertFileSize(used));
            file.setUsage(total > 0 ? Arith.round(used * 100.0 / total, 2) : 0);
            files.add(file);
        }
        // 整体替换，重复采集时不会累加上一次的磁盘记录。
        sysFiles = files;
    }

    /**
     * 将字节数转换为带单位的容量。
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format(Locale.ROOT, "%.1f GB", (double) size / gb);
        }
        if (size >= mb) {
            double value = (double) size / mb;
            return String.format(Locale.ROOT, value > 100 ? "%.0f MB" : "%.1f MB", value);
        }
        if (size >= kb) {
            double value = (double) size / kb;
            return String.format(Locale.ROOT, value > 100 ? "%.0f KB" : "%.1f KB", value);
        }
        return size + " B";
    }
}
