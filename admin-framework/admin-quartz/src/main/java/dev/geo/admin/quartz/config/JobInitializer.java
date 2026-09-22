package dev.geo.admin.quartz.config;

import dev.geo.admin.quartz.mapper.SysJobMapper;
import dev.geo.admin.quartz.model.SysJob;
import dev.geo.admin.quartz.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 初始化定时器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobInitializer implements ApplicationRunner {
    private final Scheduler scheduler;
    private final SysJobMapper sysJobMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        scheduler.clear();
        log.info("--初始化定时器 loadSystemJobs--");
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
