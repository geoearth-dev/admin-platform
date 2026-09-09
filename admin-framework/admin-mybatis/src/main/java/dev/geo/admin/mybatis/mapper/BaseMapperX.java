package dev.geo.admin.mybatis.mapper;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.mybatis.model.page.SortPageParam;
import dev.geo.admin.mybatis.model.page.SortField;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.model.converter.PageResultConverter;
import dev.geo.admin.mybatis.util.JdbcUtils;
import dev.geo.admin.mybatis.util.MyBatisUtils;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 在 MyBatis-Plus 的 BaseMapper 基础上进行扩展，提供更多能力
 * <p>
 * 1. {@link BaseMapper} 是 MyBatis-Plus 的基础接口，提供基本的 CRUD 能力。
 * 2. {@link MPJBaseMapper} 是 MyBatis-Plus-Join 的基础接口，提供多表联查能力。
 */
public interface BaseMapperX<T> extends MPJBaseMapper<T> {

    default PageResult<T> selectPage(SortPageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, pageParam.getSortingFields(), queryWrapper);
    }

    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, null, queryWrapper);
    }

    default PageResult<T> selectPage(PageParam pageParam, Collection<SortField> sortFields, @Param("ew") Wrapper<T> queryWrapper) {
        // 特殊情况：不分页，直接查询全部数据
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<T> list = selectList(queryWrapper);
            return new PageResult<>(list, (long) list.size(), 1, -1, 1);
        }

        // MyBatis-Plus 分页查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam, sortFields);
        IPage<T> result = selectPage(mpPage, queryWrapper);
        // 转换并返回分页结果
        return PageResultConverter.of(result);
    }

    default <D> PageResult<D> selectJoinPage(PageParam pageParam, Class<D> clazz, MPJLambdaWrapper<T> lambdaWrapper) {
        // 特殊情况：不分页，直接查询全部数据
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<D> list = selectJoinList(clazz, lambdaWrapper);
            return new PageResult<>(list, (long) list.size(), 1, -1, 1);
        }

        // MyBatis-Plus-Join 分页查询
        IPage<D> mpPage = MyBatisUtils.buildPage(pageParam);
        IPage<D> result = selectJoinPage(mpPage, clazz, lambdaWrapper);
        // 转换并返回分页结果
        return PageResultConverter.of(result);
    }

    default <DTO> PageResult<DTO> selectJoinPage(PageParam pageParam, Class<DTO> resultTypeClass, MPJBaseJoin<T> joinQueryWrapper) {
        IPage<DTO> mpPage = MyBatisUtils.buildPage(pageParam);
        IPage<DTO> result = selectJoinPage(mpPage, resultTypeClass, joinQueryWrapper);
        // 转换并返回分页结果
        return PageResultConverter.of(result);
    }

    default T selectOne(String field, Object value) {
        return selectOne(new QueryWrapper<T>().eq(field, value));
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(new QueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> field3, Object value3) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2).eq(field3, value3));
    }

    default Long selectCount() {
        return selectCount(new QueryWrapper<>());
    }

    default Long selectCount(String field, Object value) {
        return selectCount(new QueryWrapper<T>().eq(field, value));
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        return selectCount(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    default List<T> selectList(String field, Object value) {
        return selectList(new QueryWrapper<T>().eq(field, value));
    }

    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return selectList(new LambdaQueryWrapper<T>().eq(field, value));
    }

    default List<T> selectList(String field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return selectList(new QueryWrapper<T>().in(field, values));
    }

    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return selectList(new LambdaQueryWrapper<T>().in(field, values));
    }

    @Deprecated
    default List<T> selectList(SFunction<T, ?> leField, SFunction<T, ?> geField, Object value) {
        return selectList(new LambdaQueryWrapper<T>().le(leField, value).ge(geField, value));
    }

    default List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectList(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 批量插入，适用于插入大量数据
     *
     * @param entities 实体集合
     */
    default Boolean insertBatch(Collection<T> entities) {
        // 特殊情况：SQL Server 批量插入后获取主键 ID 会报错，因此改为循环插入
        if (Objects.equals(JdbcUtils.getDbType(), DbType.SQL_SERVER)) {
            entities.forEach(this::insert);
            return CollUtil.isNotEmpty(entities);
        }
        return Db.saveBatch(entities);
    }

    /**
     * 批量插入，适用于插入大量数据
     *
     * @param entities 实体集合
     * @param size     每批插入数量，Db.saveBatch 默认为 1000
     */
    default Boolean insertBatch(Collection<T> entities, int size) {
        // 特殊情况：SQL Server 批量插入后获取主键 ID 会报错，因此改为循环插入
        if (Objects.equals(JdbcUtils.getDbType(), DbType.SQL_SERVER)) {
            entities.forEach(this::insert);
            return CollUtil.isNotEmpty(entities);
        }
        return Db.saveBatch(entities, size);
    }

    default int updateBatch(T update) {
        return update(update, new QueryWrapper<>());
    }

    default Boolean updateBatch(Collection<T> entities) {
        return Db.updateBatchById(entities);
    }

    default Boolean updateBatch(Collection<T> entities, int size) {
        return Db.updateBatchById(entities, size);
    }

    default Boolean insertOrUpdateBatch(Collection<T> collection) {
        return Db.saveOrUpdateBatch(collection);
    }

    default int delete(String field, String value) {
        return delete(new QueryWrapper<T>().eq(field, value));
    }

    default int delete(SFunction<T, ?> field, Object value) {
        return delete(new LambdaQueryWrapper<T>().eq(field, value));
    }

}
