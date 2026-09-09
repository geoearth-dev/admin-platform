package dev.geo.admin.mybatis.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.mybatis.model.page.SortField;
import dev.geo.admin.mybatis.enums.DbTypeEnum;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MyBatis 工具类
 */
public class MyBatisUtils {

    private static final String MYSQL_ESCAPE_CHARACTER = "`";

    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }

    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortField> sortingFields) {
        // 页码和每页数量
        Page<T> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        // 排序字段
        if (!CollectionUtil.isEmpty(sortingFields)) {
            page.addOrder(sortingFields.stream().map(sortingField -> SortField.ORDER_ASC.equals(sortingField.getOrder()) ?
                            OrderItem.asc(sortingField.getField()) : OrderItem.desc(sortingField.getField()))
                    .collect(Collectors.toList()));
        }
        return page;
    }

    /**
     * 向拦截器链中添加拦截器
     * 由于 MybatisPlusInterceptor 不支持在指定位置直接添加拦截器，因此只能重新设置完整的拦截器集合
     *
     * @param interceptor 拦截器链
     * @param inner       待添加的拦截器
     * @param index       添加位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    /**
     * 获取 Table 对象对应的表名
     * <p>
     * 支持 MySQL 使用转义符包裹的表名，如 `t_xxx`
     *
     * @param table 表对象
     * @return 去除转义符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构造 Column 对象
     *
     * @param tableName  表名
     * @param tableAlias 表别名
     * @param column     字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }
        return new Column(tableName + StringPool.DOT + column);
    }

    /**
     * 跨数据库的 find_in_set 实现
     *
     * @param column 字段名
     * @param value  查询值（不包含单引号）
     * @return SQL 语句
     */
    public static String findInSet(String column, Object value) {
        DbType dbType = JdbcUtils.getDbType();
        return DbTypeEnum.getFindInSetTemplate(dbType)
                .replace("#{column}", column)
                .replace("#{value}", StrUtil.toString(value));
    }

}
