package dev.geo.admin.system.service.auth;

import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.common.exception.user.UserPasswordRetryLimitExceedException;
import dev.geo.admin.common.utils.I18nMessageUtil;
import dev.geo.admin.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserPasswordService {
    @Value("${security.login.max-retry-count:5}")
    private int maxRetryCount;

    @Value("${security.login.lock-time:10m}")
    private Duration lockTime;

    private final RedisCache redisCache;
    private final I18nMessageUtil messages;

    public void checkPasswordRetry(String username) {
        int retryCount = getPasswordRetryCount(username);
        if (retryCount >= maxRetryCount) {
            throw new UserPasswordRetryLimitExceedException(maxRetryCount, lockTime.toMinutesPart());
        }
    }

    public void recordPasswordFailure(String username) {
        String cacheKey = getPasswordRetryKey(username);

        int retryCount = getPasswordRetryCount(username) + 1;

        redisCache.setCacheObject(cacheKey, retryCount, lockTime);
    }

    public void clearPasswordRetry(String username) {
        redisCache.deleteObject(getPasswordRetryKey(username));
    }

    private int getPasswordRetryCount(String username) {
        Integer retryCount = redisCache.getCacheObject(getPasswordRetryKey(username), Integer.class);

        return retryCount == null ? 0 : retryCount;
    }

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getPasswordRetryKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }


}
