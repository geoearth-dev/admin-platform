package dev.geo.admin.system;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.system.mapper.system.*;
import dev.geo.admin.system.model.system.entity.*;
import dev.geo.admin.system.model.system.dto.NoticeReadUserPageReqDTO;
import dev.geo.admin.system.service.system.impl.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** 仅在显式指定的隔离 MySQL 测试库运行；所有测试数据在结束时回滚。 */
@EnabledIfEnvironmentVariable(named = "ADMIN_SOFT_DELETE_TEST_URL",
        matches = "jdbc:mysql://localhost:3306/admin_post_notice_check_[0-9]+")
class PostNoticeSoftDeleteTest {
    private static void inject(Object target, String name, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void sql(Connection connection, String sql) throws Exception {
        try (var statement = connection.createStatement()) { statement.execute(sql); }
    }

    private static long number(Connection connection, String sql) throws Exception {
        try (var statement = connection.createStatement(); var rows = statement.executeQuery(sql)) {
            assertTrue(rows.next());
            return rows.getLong(1);
        }
    }

    private static SysPost post(SysPostMapper mapper, String code, String name) {
        var post = new SysPost();
        post.setPostCode(code); post.setPostName(name); post.setPostSort(0); post.setStatus("1");
        mapper.insertPost(post);
        return post;
    }

    private static SysNotice notice(SysNoticeMapper mapper, String title) {
        var notice = new SysNotice();
        notice.setNoticeTitle(title); notice.setNoticeType("1"); notice.setStatus("1");
        mapper.insertNotice(notice);
        return notice;
    }

    @Test
    void softDeletePreservesHistoryAndFiltersAllBusinessQueries() throws Exception {
        var config = new MybatisConfiguration();
        var source = new UnpooledDataSource("com.mysql.cj.jdbc.Driver",
                System.getenv("ADMIN_SOFT_DELETE_TEST_URL"),
                System.getenv("ADMIN_SOFT_DELETE_TEST_USER"), System.getenv("ADMIN_SOFT_DELETE_TEST_PASSWORD"));
        config.setEnvironment(new Environment("isolated-test", new JdbcTransactionFactory(), source));
        config.setMapUnderscoreToCamelCase(true);
        var global = new GlobalConfig();
        global.setDbConfig(new GlobalConfig.DbConfig().setLogicDeleteValue("1").setLogicNotDeleteValue("0"));
        GlobalConfigUtils.setGlobalConfig(config, global);
        config.getTypeAliasRegistry().registerAlias("SysNotice", SysNotice.class);
        config.getTypeAliasRegistry().registerAlias("SysNoticeRead", SysNoticeRead.class);
        config.getTypeAliasRegistry().registerAlias("SysUserPost", SysUserPost.class);
        config.addMapper(SysPostMapper.class); config.addMapper(SysNoticeMapper.class);
        for (String name : List.of("SysPostRelationsMapper.xml", "SysNoticeReadMapper.xml", "SysUserPostMapper.xml")) {
            String path = "mapper/system/" + name;
            try (var input = getClass().getClassLoader().getResourceAsStream(path)) {
                assertNotNull(input);
                new XMLMapperBuilder(input, config, path, config.getSqlFragments()).parse();
            }
        }
        try (SqlSession session = new MybatisSqlSessionFactoryBuilder().build(config).openSession(false)) {
            try {
                var connection = session.getConnection();
                var posts = session.getMapper(SysPostMapper.class);
                var notices = session.getMapper(SysNoticeMapper.class);
                var reads = session.getMapper(SysNoticeReadMapper.class);
                var userPosts = session.getMapper(SysUserPostMapper.class);
                var postService = new SysPostServiceImpl();
                inject(postService, "postMapper", posts); inject(postService, "userPostMapper", userPosts);
                var noticeService = new SysNoticeServiceImpl(); inject(noticeService, "noticeMapper", notices);
                var readService = new SysNoticeReadServiceImpl(reads, notices);
                var userService = new SysUserServiceImpl(null, null, posts, null, userPosts, null, null, null);
                long userId = number(connection, "SELECT id FROM sys_user WHERE del_flag=0 LIMIT 1");
                String key = "soft_test_" + System.nanoTime();
                var original = post(posts, key, key);
                assertThrows(RuntimeException.class, () -> post(posts, key, key + "other"));
                assertThrows(RuntimeException.class, () -> post(posts, key + "other", key));
                var user = new SysUser(); user.setId(userId); user.setPostIds(new Long[]{original.getId()});
                userService.insertUserPost(user);
                assertThrows(ServiceException.class, () -> postService.deletePostById(original.getId()));
                assertNotNull(posts.selectPostById(original.getId()));
                sql(connection, "DELETE FROM sys_user_post WHERE user_id=" + userId + " AND post_id=" + original.getId());
                session.clearCache();
                assertEquals(1, postService.deletePostById(original.getId()));
                assertEquals(1, number(connection, "SELECT del_flag FROM sys_post WHERE id=" + original.getId()));
                assertNull(posts.selectPostById(original.getId()));
                assertNull(posts.checkPostCodeUnique(key)); assertNull(posts.checkPostNameUnique(key));
                assertTrue(posts.selectPostAll().stream().noneMatch(p -> p.getId().equals(original.getId())));
                assertTrue(posts.selectPostList(new SysPost()).stream().noneMatch(p -> p.getId().equals(original.getId())));
                assertEquals(0, posts.updatePost(original));
                assertThrows(ServiceException.class, () -> userService.insertUserPost(user));
                assertThrows(ServiceException.class, () -> postService.deletePostById(original.getId()));
                sql(connection, "INSERT INTO sys_user_post(user_id,post_id) VALUES (" + userId + "," + original.getId() + ")");
                session.clearCache();
                assertFalse(posts.selectPostListByUserId(userId).contains(original.getId()));
                String userName;
                try (var statement = connection.createStatement(); var rows = statement.executeQuery("SELECT user_name FROM sys_user WHERE id=" + userId)) {
                    rows.next(); userName = rows.getString(1);
                }
                assertTrue(posts.selectPostsByUserName(userName).stream().noneMatch(p -> p.getId().equals(original.getId())));
                var replacement = post(posts, key, key);
                postService.deletePostById(replacement.getId());
                assertNotNull(post(posts, key, key).getId());

                int before = reads.selectUnreadCount(userId);
                var readNotice = notice(notices, key); var unreadNotice = notice(notices, key + " unread");
                readService.markRead(readNotice.getId(), userId);
                readService.markRead(readNotice.getId(), userId);
                assertEquals(before + 1, reads.selectUnreadCount(userId));
                assertEquals(1, reads.selectIsRead(readNotice.getId(), userId));
                assertEquals(2, noticeService.deleteNoticeByIds(new Long[]{readNotice.getId(), unreadNotice.getId()}));
                assertEquals(1, number(connection, "SELECT COUNT(*) FROM sys_notice_read WHERE notice_id=" + readNotice.getId() + " AND user_id=" + userId));
                assertEquals(1, number(connection, "SELECT del_flag FROM sys_notice WHERE id=" + readNotice.getId()));
                assertNull(notices.selectNoticeById(readNotice.getId()));
                assertEquals(0, notices.updateNotice(readNotice));
                assertEquals(before, reads.selectUnreadCount(userId));
                assertEquals(0, reads.selectIsRead(readNotice.getId(), userId));
                assertTrue(reads.selectNoticeListWithReadStatus(userId, 1000).stream()
                        .noneMatch(n -> n.getId().equals(readNotice.getId()) || n.getId().equals(unreadNotice.getId())));
                assertTrue(notices.selectNoticeList(new SysNotice()).stream().noneMatch(n -> n.getId().equals(readNotice.getId())));
                assertThrows(ServiceException.class, () -> readService.markRead(readNotice.getId(), userId));
                assertThrows(ServiceException.class, () -> readService.markReadBatch(userId, new Long[]{unreadNotice.getId()}));
                var record = new SysNoticeRead(); record.setNoticeId(unreadNotice.getId()); record.setUserId(userId);
                assertEquals(0, reads.insertNoticeRead(record));
                assertEquals(0, reads.insertNoticeReadBatch(userId, new Long[]{unreadNotice.getId()}));
                var query = new NoticeReadUserPageReqDTO(); query.setNoticeId(readNotice.getId());
                assertThrows(ServiceException.class, () -> readService.selectReadUsersPage(query));
                sql(connection, "UPDATE sys_notice SET del_flag=0 WHERE id=" + readNotice.getId());
                session.clearCache();
                assertEquals(1, reads.selectIsRead(readNotice.getId(), userId));
                assertEquals(before, reads.selectUnreadCount(userId));
            } finally {
                session.rollback(true);
            }
        }
    }
}
