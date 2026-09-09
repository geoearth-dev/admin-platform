package dev.geo.admin.system.resolve.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Instant now = Instant.now();
        strictInsertFill(metaObject, "createTime", Instant.class, now);
        strictInsertFill(metaObject, "updateTime", Instant.class, now);

        LoginPrincipal principal = currentPrincipal();
        if (principal != null) {
            strictInsertFill(metaObject, "creatorId", Long.class, principal.getUserId());
            strictInsertFill(metaObject, "createBy", String.class, principal.getNickName());
            strictInsertFill(metaObject, "updaterId", Long.class, principal.getUserId());
            strictInsertFill(metaObject, "updateBy", String.class, principal.getNickName());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", Instant.class, Instant.now());

        LoginPrincipal principal = currentPrincipal();
        if (principal != null) {
            strictUpdateFill(metaObject, "updaterId", Long.class, principal.getUserId());
            strictUpdateFill(metaObject, "updateBy", String.class, principal.getNickName());
        }
    }

    /**
     * 获取当前登录主体；匿名请求和系统任务返回 {@code null}。
     */
    private LoginPrincipal currentPrincipal() {
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginPrincipal principal) {
            return principal;
        }
        return null;
    }
}
