package dev.geo.admin.system.service.system;

import com.baomidou.mybatisplus.spring.service.IService;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.model.system.dto.SysConfigPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysConfig;

/**
 * 系统参数配置服务。
 */
public interface ISysConfigService extends IService<SysConfig> {
    /**
     * 根据主键查询参数配置。
     *
     * @param id 参数配置ID
     * @return 参数配置信息
     */
    SysConfig getConfigById(Long id);

    /**
     * 根据参数键名查询参数值。
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    String selectConfigByKey(String configKey);

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    boolean selectCaptchaEnabled();

    /**
     * 分页查询参数配置。
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<SysConfig> getConfigPage(SysConfigPageReqDTO query);

    /**
     * 新增参数配置。
     *
     * @param config 参数配置信息
     * @return 结果
     */
    int createConfig(SysConfig config);

    /**
     * 修改参数配置。
     *
     * @param config 参数配置信息
     * @return 结果
     */
    int updateConfig(SysConfig config);

    /**
     * 批量删除参数配置。
     *
     * @param ids 需要删除的参数ID
     */
    void deleteConfigs(Long[] ids);

    /**
     * 加载参数缓存数据
     */
    void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

}
