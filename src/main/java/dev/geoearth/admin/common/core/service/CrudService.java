package dev.geoearth.admin.common.core.service;


import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.response.PageResponse;

/**
 * 后台配置类资源的通用业务接口。
 *
 * @param <T> 业务实体类型
 */
public interface CrudService<T> {

    /**
     * 分页查询资源，可按业务编码或名称进行模糊搜索。
     */
    PageResponse<T> page(PageParam pageParam, String keyword);

    /**
     * 根据主键查询资源，不存在或已逻辑删除时抛出业务异常。
     */
    T getRequired(Object id);

    /**
     * 新增资源，主键、逻辑删除和审计时间由系统维护。
     */
    T create(T entity);

    /**
     * 根据主键更新资源，不允许调用方修改创建审计字段。
     */
    T update(Object id, T entity);

    /**
     * 逻辑删除资源。
     */
    void delete(Object id);
}
