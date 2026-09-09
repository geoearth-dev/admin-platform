package dev.geo.admin.system.resolve.runner;

import dev.geo.admin.system.service.system.ISysConfigService;
import dev.geo.admin.system.service.system.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * 项目启动时，初始化字典到缓存
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class SysCacheRunner implements ApplicationRunner {

    private final ISysDictTypeService sysDictService;
    private final ISysConfigService sysConfigService;

    @Override
    public void run(@Nullable ApplicationArguments args) throws Exception {
        log.info("----开始初始化缓存----");
        try {
            log.info("--字典缓存--");
            sysDictService.loadingDictCache();
            log.info("--config缓存--");
            sysConfigService.loadingConfigCache();

            log.info("----缓存初始化完成----");
        } catch (Exception exception) {
            log.error("----缓存初始化失败，项目继续启动----", exception);
        }
    }
}
