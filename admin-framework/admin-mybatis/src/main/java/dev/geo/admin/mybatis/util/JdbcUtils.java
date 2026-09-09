package dev.geo.admin.mybatis.util;


import com.baomidou.mybatisplus.annotation.DbType;
import dev.geo.admin.common.utils.spring.SpringUtils;
import dev.geo.admin.mybatis.enums.DbTypeEnum;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC 工具类
 *
 * @author taro 源码
 */
public class JdbcUtils {

    /**
     * 判断数据库连接是否正常
     *
     * @param url      数据源连接地址
     * @param username 账号
     * @param password 密码
     * @return 是否连接正常
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获取 URL 对应的数据库类型
     *
     * @param url URL
     * @return 数据库类型
     */
    public static DbType getDbType(String url) {
        return com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(url);
    }

    /**
     * 通过当前数据库连接获取对应的数据库类型
     *
     * @return 数据库类型
     */
    public static DbType getDbType() {
//        DynamicRoutingDataSource dynamicRoutingDataSource = SpringUtils.getBean(DynamicRoutingDataSource.class);
//        DataSource dataSource = dynamicRoutingDataSource.determineDataSource();

        DataSource dataSource = SpringUtils.getBean(DataSource.class);
        try (Connection conn = dataSource.getConnection()) {
            return DbTypeEnum.find(conn.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }

}
