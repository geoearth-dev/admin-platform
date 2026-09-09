package dev.geo.admin.system.service.auth;

import cn.hutool.core.util.ObjectUtil;
import dev.geo.admin.common.enums.UserStatus;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.common.utils.I18nMessageUtil;
import dev.geo.admin.common.utils.ServletUtils;
import dev.geo.admin.common.utils.http.UserAgentUtils;
import dev.geo.admin.common.utils.ip.AddressUtils;
import dev.geo.admin.common.utils.ip.IpUtils;
import dev.geo.admin.security.model.LoginPrincipal;
import dev.geo.admin.security.model.LoginUserInfo;
import dev.geo.admin.security.model.RoleGrant;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.service.system.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户验证处理
 */
@Service
@NullMarked
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final ISysUserService userService;
    private final UserPermissionService permissionService;
    private final I18nMessageUtil messages;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException(messages.get("user.not.exists"));
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(messages.get("user.password.delete"));
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new DisabledException(messages.get("user.blocked"));
        }
        return createLoginPrincipal(user);

    }

    private LoginPrincipal createLoginPrincipal(SysUser user) {
        Set<String> permissions = permissionService.getMenuPermission(user);
        Set<RoleGrant> roleGrants = Optional
                .ofNullable(user.getRoles())
                .orElseGet(List::of)
                .stream()
                .map(role ->
                        new RoleGrant(
                                role.getId(),
                                role.getDataScope(),
                                role.getRoleKey(),
                                role.getPermissions()
                        ))
                .collect(Collectors.toUnmodifiableSet());
        String ip = IpUtils.getIp();
        String userAgent = ServletUtils.getRequest().getHeader("User-Agent");
        LoginUserInfo userInfo = new LoginUserInfo(
                user.getId(),
                user.getUserName(),
                user.getNickName(),
                user.getAvatar(),
                user.getPasswordUpdateTime(),
                ip,
                AddressUtils.getRealAddressByIP(ip),
                UserAgentUtils.getBrowser(userAgent),
                UserAgentUtils.getOperatingSystem(userAgent),
                user.getDeptId(),
                user.getDept().getDeptName(),
                roleGrants,
                permissions
        );
        return new LoginPrincipal(userInfo, user.getPassword());
    }
}
