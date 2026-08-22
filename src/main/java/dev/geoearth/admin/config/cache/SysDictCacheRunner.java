package dev.geoearth.admin.config.cache;

import dev.geoearth.admin.service.dictionary.SysDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class SysDictCacheRunner implements ApplicationRunner {

    private final SysDictService sysDictService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化字典缓存");
        try {
            sysDictService.reloadAllDictData();
            log.info("字典缓存初始化完成");
        } catch (Exception exception) {
            log.error("字典缓存初始化失败，项目继续启动", exception);
        }
    }
}
