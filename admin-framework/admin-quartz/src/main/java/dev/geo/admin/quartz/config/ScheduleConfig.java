package dev.geo.admin.quartz.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.quartz.autoconfigure.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 使用 Spring Boot 自动配置的 Scheduler，在任务恢复完毕后启动。 */
@Configuration(proxyBeanMethods = false)
@MapperScan("dev.geo.admin.quartz.mapper")
public class ScheduleConfig {
    @Bean
    public SchedulerFactoryBeanCustomizer quartzStartupCustomizer() {
        return factory -> factory.setAutoStartup(false);
    }
}
