package dev.geo.admin.system.service.system.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import dev.geo.admin.common.constant.CacheConstants;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.redis.RedisCache;
import dev.geo.admin.system.mapper.system.SysConfigMapper;
import dev.geo.admin.system.model.system.dto.SysConfigPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysConfig;
import dev.geo.admin.system.service.system.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 系统参数配置服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {
    private final SysConfigMapper configMapper;

    private final RedisCache redisCache;

    /**
     * 根据主键查询参数配置。
     *
     * @param id 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig getConfigById(Long id) {
        SysConfig config = configMapper.selectById(id);
        if (config == null) {
            throw new ServiceException("参数配置不存在或已被删除");
        }
        return config;
    }

    /**
     * 根据参数键名查询参数值。
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey), String.class));
        if (StrUtil.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig retConfig = configMapper.selectByConfigKey(configKey);
        if (retConfig != null) {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled() {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StrUtil.isEmpty(captchaEnabled)) {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 分页查询参数配置。
     *
     * @param query 分页查询条件
     * @return 参数配置集合
     */
    @Override
    public PageResult<SysConfig> getConfigPage(SysConfigPageReqDTO query) {
        if (query.getBeginDate() != null && query.getEndDate() != null
                && query.getBeginDate().isAfter(query.getEndDate())) {
            throw new ServiceException("创建日期起始值不能晚于结束值");
        }
        return configMapper.selectPage(query);
    }

    /**
     * 新增参数配置。
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int createConfig(SysConfig config) {
        // 当前 sys_config 使用数据库自增主键，不接受客户端指定 ID。
        config.setId(null);
        if (!isConfigKeyUnique(config)) {
            throw new ServiceException("参数键名已存在：" + config.getConfigKey());
        }
        int row = configMapper.insert(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置。
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConfig(SysConfig config) {
        if (config.getId() == null) {
            throw new ServiceException("参数配置ID不能为空");
        }
        SysConfig temp = configMapper.selectById(config.getId());
        if (temp == null) {
            throw new ServiceException("参数配置不存在或已被删除");
        }
        if (!isConfigKeyUnique(config)) {
            throw new ServiceException("参数键名已存在：" + config.getConfigKey());
        }
        if (!StrUtil.equals(temp.getConfigKey(), config.getConfigKey())) {
            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateById(config);
        if (row > 0) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数配置。
     *
     * @param ids 需要删除的参数ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigs(Long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new ServiceException("请选择需要删除的参数配置");
        }
        List<Long> idList = Arrays.stream(ids)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (idList.isEmpty()) {
            throw new ServiceException("参数配置ID不能为空");
        }
        List<SysConfig> configs = configMapper.selectByIds(idList);
        if (configs.size() != idList.size()) {
            throw new ServiceException("部分参数配置不存在或已被删除");
        }
        for (SysConfig config : configs) {
            if (StrUtil.equals(UserConstants.YES, config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除", config.getConfigKey()));
            }
        }
        configMapper.deleteByIds(idList);
        for (SysConfig config : configs) {
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        for (SysConfig config : configMapper.selectList()) {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 判断参数键名是否可用。
     */
    private boolean isConfigKeyUnique(SysConfig config) {
        SysConfig existing = configMapper.selectByConfigKey(config.getConfigKey());
        return existing == null || Objects.equals(existing.getId(), config.getId());
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
