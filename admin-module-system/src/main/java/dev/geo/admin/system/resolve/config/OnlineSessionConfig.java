package dev.geo.admin.system.resolve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/** 在线连接维护 调度线程 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
public class OnlineSessionConfig {
    @Bean
    public ThreadPoolTaskScheduler onlineSessionScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("online-session-");
        return scheduler;
    }
}
