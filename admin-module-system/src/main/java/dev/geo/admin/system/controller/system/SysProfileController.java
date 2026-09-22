package dev.geo.admin.system.controller.system;

import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.config.AppConfig;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.common.utils.file.FileUploadUtils;
import dev.geo.admin.security.session.LoginSessionStore;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.system.dto.ProfileUpdateDTO;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.model.system.vo.UserProfileVO;
import dev.geo.admin.system.service.system.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import dev.geo.admin.system.model.system.dto.PasswordChangeDTO;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * 当前用户个人资料接口。
 */
@Tag(name = "个人资料")
@RestController
@RequestMapping("/system/profile")
@RequiredArgsConstructor
public class SysProfileController extends BaseController {
    private final ISysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final LoginSessionStore sessionStore;

    @GetMapping
    @Operation(summary = "获取我的个人资料")
    public ApiResult<UserProfileVO> profile() {
        return success(toProfile(currentUser()));
    }

    @Log(title = "个人资料", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改我的个人资料")
    public ApiResult<UserProfileVO> updateProfile(@Validated @RequestBody ProfileUpdateDTO request) {
        SysUser user = new SysUser(SecurityUtils.getUserId());
        user.setNickName(request.nickName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setSex(request.sex());
        user.setRemark(request.remark());
        if (!userService.checkPhoneUnique(user)) {
            return ApiResult.error("手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return ApiResult.error("邮箱已存在");
        }
        if (userService.updateUserProfile(user) <= 0) {
            return ApiResult.error("个人资料更新失败");
        }
        SysUser current = currentUser();
        syncUserSessions(current);
        return success(toProfile(current));
    }

    @Log(title = "个人头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    @Operation(summary = "修改我的头像")
    public ApiResult<UserProfileVO> updateAvatar(@RequestParam("file") MultipartFile file) throws Exception {
        SysUser current = currentUser();
        validateAvatar(file);
        String avatar = FileUploadUtils.upload(AppConfig.getAvatarPath(), file,
                new String[]{"jpg", "jpeg", "png"}, true);
        if (!userService.updateUserAvatar(current.getId(), avatar)) {
            return ApiResult.error("头像保存失败");
        }
        current = currentUser();
        syncUserSessions(current);
        return success(toProfile(current));
    }

    @Log(title = "个人资料", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    @Operation(summary = "修改我的密码")
    public ApiResult<Void> updatePassword(@Validated @RequestBody PasswordChangeDTO request) {
        SysUser current = currentUser();
        if (!passwordEncoder.matches(request.oldPassword(), current.getPassword())) {
            return error("原密码不正确");
        }
        if (passwordEncoder.matches(request.newPassword(), current.getPassword())) {
            return error("新密码不能与原密码相同");
        }
        int rows = userService.resetUserPwd(current.getId(), passwordEncoder.encode(request.newPassword()));
        if (rows > 0) {
            sessionStore.findAll().stream()
                    .filter(session -> Objects.equals(session.userInfo().userId(), current.getId()))
                    .forEach(session -> sessionStore.delete(session.sessionId()));
        }
        return toApiResult(rows);
    }

    private SysUser currentUser() {
        SysUser user = userService.selectUserById(SecurityUtils.getUserId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    private UserProfileVO toProfile(SysUser user) {
        return new UserProfileVO(user.getId(), user.getUserName(), user.getNickName(),
                user.getEmail(), user.getPhoneNumber(), user.getSex(), user.getAvatar(), user.getRemark(),
                user.getDept() == null ? "" : user.getDept().getDeptName(),
                userService.selectUserRoleGroup(user.getUserName()),
                userService.selectUserPostGroup(user.getUserName()), user.getCreateTime());
    }

    private void syncUserSessions(SysUser user) {
        sessionStore.findAll().stream()
                .filter(session -> Objects.equals(session.userInfo().userId(), user.getId()))
                .forEach(session -> sessionStore.updateUserInfo(session.sessionId(),
                        session.userInfo().withProfile(user.getNickName(), user.getEmail(), user.getAvatar())));
    }

    private void validateAvatar(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > 2 * 1024 * 1024) {
            throw new ServiceException("请选择不超过 2 MB 的头像图片");
        }
        try (var source = file.getInputStream();
             var input = ImageIO.createImageInputStream(source)) {
            if (input == null) {
                throw new ServiceException("头像必须是 JPG 或 PNG 图片");
            }
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw new ServiceException("头像必须是 JPG 或 PNG 图片");
            }
            var reader = readers.next();
            try {
                reader.setInput(input);
                String format = reader.getFormatName();
                if (!("JPEG".equalsIgnoreCase(format) || "PNG".equalsIgnoreCase(format))) {
                    throw new ServiceException("头像必须是 JPG 或 PNG 图片");
                }
                if (reader.getWidth(0) > 4096 || reader.getHeight(0) > 4096) {
                    throw new ServiceException("头像图片宽高不能超过 4096 像素");
                }
            } finally {
                reader.dispose();
            }
        }
    }
}
