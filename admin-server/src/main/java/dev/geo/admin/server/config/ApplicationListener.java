package dev.geo.admin.server.config;

import dev.geo.admin.common.utils.ip.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.Duration;
import java.util.Enumeration;

@Component
public class ApplicationListener {
    private static final Logger log = LoggerFactory.getLogger(ApplicationListener.class);

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {

        Environment env = event.getApplicationContext().getEnvironment();
        // 获取应用名称
        String appName = env.getProperty("spring.application.name", "application");
        // 获取端口
        String port = env.getProperty("server.port", "8080");

        // 判断是否启用 HTTPS
        String protocol = (env.getProperty("server.ssl.key-store") != null) ? "https" : "http";

        // 获取本机 IP
        String externalIp = IpUtils.getHostIp();
        String apiPath = env.getProperty("scalar.path", "/api");

        // 获取激活的 profile（环境）
        String[] activeProfiles = env.getActiveProfiles();
        String profiles = activeProfiles.length == 0 ? "[]" : String.join(", ", activeProfiles);

        Duration timeTaken = event.getTimeTaken();
        long timeMillis = timeTaken == null ? 0 : timeTaken.toMillis();
        log.info("""
                        
                        ------------------------------------------------------------
                           {} 启动成功
                           本机地址：{}://localhost:{}
                           服务地址：{}://{}:{}
                           接口文档：{}://{}:{}{}
                           配置环境：{}
                           启动耗时：{} ms
                        ------------------------------------------------------------
                        """,
                appName,
                protocol, port,
                protocol, externalIp, port,
                protocol, externalIp, port, apiPath,
                profiles,
                timeMillis
        );
    }


}
