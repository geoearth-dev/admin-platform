package dev.geoearth.admin.common.aspect;

import dev.geoearth.admin.common.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogAspect {
    @Around("@annotation(controllerLog)")
    public Object around(ProceedingJoinPoint joinPoint, Log controllerLog) throws Throwable {
        long startTime = System.nanoTime();
        try {
            //TODO 操作日志入库
            return null;
        } catch (Throwable throwable) {
            //@AfterThrowing 抛出，让全局异常处理器处理
            throw throwable;
        } finally {
            // @After
            long costTime = System.nanoTime() - startTime;
        }
    }
}
