package dev.geo.admin.system.resolve.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@MapperScan(basePackages = "dev.geo.admin.system.mapper")
public class SystemMybatisConfig {
}
