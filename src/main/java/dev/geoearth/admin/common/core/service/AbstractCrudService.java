package dev.geoearth.admin.common.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.exception.BusinessException;
import dev.geoearth.admin.common.core.response.PageResponse;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 后台配置类表的通用增删改查实现。
 *
 * <p>写入前会清理主键、逻辑删除和创建审计字段，避免调用方越权修改系统字段。</p>
 */
public abstract class AbstractCrudService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements CrudService<T> {

    private final Class<T> entityClass;
    private final String[] searchableColumns;

    protected AbstractCrudService(Class<T> entityClass, String... searchableColumns) {
        this.entityClass = entityClass;
        this.searchableColumns = searchableColumns;
    }

    @Override
    public PageResponse<T> page(PageParam pageParam, String keyword) {
        TableInfo tableInfo = requireTableInfo();
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword) && searchableColumns.length > 0) {
            wrapper.and(nested -> {
                for (int index = 0; index < searchableColumns.length; index++) {
                    if (index == 0) {
                        nested.like(searchableColumns[index], keyword);
                    } else {
                        nested.or().like(searchableColumns[index], keyword);
                    }
                }
            });
        }
        wrapper.orderByDesc(tableInfo.getKeyColumn());
        Page<T> page = Page.of(pageParam.getPageNum(), pageParam.getPageSize());
        return PageResponse.of(page(page, wrapper));
    }

    @Override
    public T getRequired(Object id) {
        T entity = getById((java.io.Serializable) id);
        if (entity == null) {
            throw new BusinessException(404, "数据不存在或已删除");
        }
        return entity;
    }

    @Override
    public T create(T entity) {
        sanitizeForCreate(entity);
        if (!save(entity)) {
            throw new BusinessException("新增数据失败");
        }
        return entity;
    }

    @Override
    public T update(Object id, T entity) {
        MetaObject metaObject = SystemMetaObject.forObject(entity);
        setIfPresent(metaObject, requireTableInfo().getKeyProperty(), id);
        clearIfPresent(metaObject, "delFlag");
        clearIfPresent(metaObject, "createBy");
        clearIfPresent(metaObject, "creatorId");
        clearIfPresent(metaObject, "createTime");
        if (!updateById(entity)) {
            throw new BusinessException(404, "数据不存在或已删除");
        }
        return getRequired(id);
    }

    @Override
    public void delete(Object id) {
        getRequired(id);
        if (!removeById((java.io.Serializable) id)) {
            throw new BusinessException("删除数据失败");
        }
    }

    private void sanitizeForCreate(T entity) {
        MetaObject metaObject = SystemMetaObject.forObject(entity);
        clearIfPresent(metaObject, requireTableInfo().getKeyProperty());
        setIfPresent(metaObject, "delFlag", "0");
        clearIfPresent(metaObject, "createTime");
        clearIfPresent(metaObject, "updateTime");
    }

    private TableInfo requireTableInfo() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        if (tableInfo == null) {
            throw new IllegalStateException("未找到MyBatis-Plus表信息：" + entityClass.getName());
        }
        return tableInfo;
    }

    private void clearIfPresent(MetaObject metaObject, String property) {
        if (metaObject.hasSetter(property)) {
            metaObject.setValue(property, null);
        }
    }

    private void setIfPresent(MetaObject metaObject, String property, Object value) {
        if (metaObject.hasSetter(property)) {
            metaObject.setValue(property, value);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + Arrays.toString(searchableColumns);
    }
}
