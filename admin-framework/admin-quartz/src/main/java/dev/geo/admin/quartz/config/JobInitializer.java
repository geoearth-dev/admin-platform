package dev.geo.admin.quartz.config;

import dev.geo.admin.quartz.mapper.SysJobMapper;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import org.quartz.Scheduler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
 */
@Component
@RequiredArgsConstructor
public class JobInitializer implements ApplicationRunner {
    private final Scheduler scheduler;
    private final SysJobMapper sysJobMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scheduler.clear();
        loadSystemJobs();
        scheduler.start();
    }

    private void loadSystemJobs() throws Exception {
        List<SysJob> jobList = sysJobMapper.selectJobAll();
        for (SysJob job : jobList) {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

}
