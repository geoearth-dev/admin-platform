package dev.geo.admin.system.controller.system;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.system.dto.ProfileUpdateDTO;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.service.system.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import dev.geo.admin.system.model.system.dto.PasswordChangeDTO;

/**
 * 当前用户个人资料接口。
 */
@RestController
@RequestMapping("/system/profile")
@RequiredArgsConstructor
public class SysProfileController extends BaseController {
    private final ISysUserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ApiResult<SysUser> profile() {
        return success(userService.selectUserById(SecurityUtils.getUserId()));
    }

    @Log(title = "个人资料", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> updateProfile(@Validated @RequestBody ProfileUpdateDTO request) {
        SysUser user = new SysUser(SecurityUtils.getUserId());
        user.setNickName(request.nickName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setSex(request.sex());
        if (!userService.checkPhoneUnique(user)) {
            return error("手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return error("邮箱已存在");
        }
        return toApiResult(userService.updateUserProfile(user));
    }

    @Log(title = "个人资料", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    public ApiResult<Void> updatePassword(@Validated @RequestBody PasswordChangeDTO request) {
        SysUser current = userService.selectUserById(SecurityUtils.getUserId());
        if (!passwordEncoder.matches(request.oldPassword(), current.getPassword())) {
            return error("原密码不正确");
        }
        if (passwordEncoder.matches(request.newPassword(), current.getPassword())) {
            return error("新密码不能与原密码相同");
        }
        return toApiResult(userService.resetUserPwd(current.getId(), passwordEncoder.encode(request.newPassword())));
    }
}
