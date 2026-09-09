package dev.geo.admin.security.aspectj;

import cn.hutool.core.util.ObjectUtil;
import dev.geo.admin.common.annotation.RateLimiter;
import dev.geo.admin.common.enums.LimitType;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.common.utils.ip.IpUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 限流处理
 */
@Aspect
@Component
public class RateLimiterAspect {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisScript<Long> limitScript;

    public RateLimiterAspect(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("limitScript") RedisScript<Long> limitScript
    ) {
        this.redisTemplate = redisTemplate;
        this.limitScript = limitScript;
    }


    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) throws Throwable {
        int time = rateLimiter.time();
        int count = rateLimiter.count();

        String key = getCombineKey(rateLimiter, point);
        try {
            Long current = redisTemplate.execute(limitScript, List.of(key), count, time);
            if (current == null) {
                throw new IllegalStateException("Redis 限流脚本返回结果为空");
            }
            if (current > count) {
                log.warn("请求触发限流：limit={}, current={}, key={}", count, current, key);
                throw new ServiceException("访问过于频繁，请稍候再试");
            }
            log.debug("限流统计：limit={}, current={}, key={}", count, current, key);
        } catch (Exception e) {
            log.error("执行限流脚本失败，key={}", key, e);
            throw new IllegalStateException("服务器限流异常，请稍候再试", e);
        }


    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        StringBuilder stringBuffer = new StringBuilder(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            stringBuffer.append(IpUtils.getIp()).append("-");
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("-").append(method.getName());
        return stringBuffer.toString();
    }
}
