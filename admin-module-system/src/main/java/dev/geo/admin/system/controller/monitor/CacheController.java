package dev.geo.admin.system.controller.monitor;

import com.alibaba.fastjson2.JSON;
import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.system.model.monitor.entity.SysCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Redis 缓存监控接口。
 */
@RestController
@RequestMapping("/monitor/cache")
@RequiredArgsConstructor
public class CacheController {
    private static final List<SysCache> CACHE_GROUPS = List.of(
            new SysCache(CacheConstants.LOGIN_TOKEN_KEY, "登录会话"),
            new SysCache(CacheConstants.SYS_CONFIG_KEY, "系统参数"),
            new SysCache(CacheConstants.SYS_DICT_KEY, "数据字典"),
            new SysCache(CacheConstants.CAPTCHA_CODE_KEY, "图形验证码"),
            new SysCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重复提交"),
            new SysCache(CacheConstants.RATE_LIMIT_KEY, "接口限流"),
            new SysCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数")
    );

    private final RedisTemplate<String, Object> redisTemplate;

    @PreAuthorize("@se.hasPermission('monitor:cache:list')")
    @GetMapping
    public ApiResult<Map<String, Object>> getInfo() {
        Properties info = redisTemplate.execute((RedisCallback<Properties>) connection -> connection.info());
        Properties commandStats = redisTemplate.execute(
                (RedisCallback<Properties>) connection -> connection.info("commandstats")
        );
        Long dbSize = redisTemplate.execute((RedisCallback<Long>) connection -> connection.dbSize());

        List<Map<String, Object>> commands = new ArrayList<>();
        if (commandStats != null) {
            commandStats.stringPropertyNames().forEach(key -> commands.add(Map.of(
                    "name", key.replaceFirst("^cmdstat_", ""),
                    "calls", extractCalls(commandStats.getProperty(key, ""))
            )));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("info", info == null ? Map.of() : info);
        result.put("dbSize", dbSize == null ? 0L : dbSize);
        result.put("commandStats", commands);
        return ApiResult.success(result);
    }

    @PreAuthorize("@se.hasPermission('monitor:cache:list')")
    @GetMapping("/groups")
    public ApiResult<List<SysCache>> getGroups() {
        return ApiResult.success(CACHE_GROUPS);
    }

    @PreAuthorize("@se.hasPermission('monitor:cache:list')")
    @GetMapping("/keys/{cacheName}")
    public ApiResult<SortedSet<String>> getKeys(@PathVariable String cacheName) {
        Set<String> keys = redisTemplate.keys(cacheName + "*");
        return ApiResult.success(keys == null ? new TreeSet<>() : new TreeSet<>(keys));
    }

    @PreAuthorize("@se.hasPermission('monitor:cache:list')")
    @GetMapping("/value")
    public ApiResult<SysCache> getValue(@RequestParam String cacheName, @RequestParam String cacheKey) {
        DataType type = redisTemplate.type(cacheKey);
        Object value = switch (type == null ? DataType.NONE : type) {
            case LIST -> redisTemplate.opsForList().range(cacheKey, 0, -1);
            case SET -> redisTemplate.opsForSet().members(cacheKey);
            case ZSET -> redisTemplate.opsForZSet().rangeWithScores(cacheKey, 0, -1);
            case HASH -> redisTemplate.opsForHash().entries(cacheKey);
            case STRING -> redisTemplate.opsForValue().get(cacheKey);
            default -> null;
        };
        return ApiResult.success(new SysCache(cacheName, cacheKey, JSON.toJSONString(value)));
    }

    @PreAuthorize("@se.hasPermission('monitor:cache:remove')")
    @DeleteMapping("/groups/{cacheName}")
    public ApiResult<Void> clearGroup(@PathVariable String cacheName) {
        Set<String> keys = redisTemplate.keys(cacheName + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return ApiResult.success();
    }

    @PreAuthorize("@se.hasPermission('monitor:cache:remove')")
    @DeleteMapping("/keys")
    public ApiResult<Void> clearKey(@RequestParam String cacheKey) {
        redisTemplate.delete(cacheKey);
        return ApiResult.success();
    }

    private static long extractCalls(String commandStats) {
        for (String item : commandStats.split(",")) {
            if (item.startsWith("calls=")) {
                try {
                    return Long.parseLong(item.substring("calls=".length()));
                } catch (NumberFormatException ignored) {
                    return 0L;
                }
            }
        }
        return 0L;
    }
}
