package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.system.entity.SysDept;
import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.model.system.dto.PasswordResetDTO;
import dev.geo.admin.system.model.system.dto.RoleIdsDTO;
import dev.geo.admin.system.model.system.dto.StatusUpdateDTO;
import dev.geo.admin.system.model.system.dto.UserSaveDTO;
import dev.geo.admin.system.model.system.dto.UserPageReqDTO;
import dev.geo.admin.system.model.system.vo.TreeSelect;
import dev.geo.admin.system.model.system.vo.UserDetailVO;
import dev.geo.admin.system.model.system.vo.UserRoleGrantVO;
import dev.geo.admin.system.service.system.ISysDeptService;
import dev.geo.admin.system.service.system.ISysPostService;
import dev.geo.admin.system.service.system.ISysRoleService;
import dev.geo.admin.system.service.system.ISysUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 用户管理接口。
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {
    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysDeptService deptService;
    private final ISysPostService postService;
    private final PasswordEncoder passwordEncoder;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:user:list')")
    @GetMapping("/list")
    public ApiResult<PageResult<SysUser>> list(@Validated UserPageReqDTO query) {
        return success(userService.selectUserPage(query));
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, @Validated UserPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, userService.selectUserPage(query).getRecords(), SysUser.class, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@se.hasPermission('system:user:import')")
    @PostMapping("/import")
    public ApiResult<Void> importData(@RequestPart MultipartFile file,
                                      @RequestParam(defaultValue = "false") boolean updateSupport) throws IOException {
        List<SysUser> users = excelService.importExcel(file.getInputStream(), SysUser.class);
        return success(userService.importUser(users, updateSupport, SecurityUtils.getUsername()));
    }

    @GetMapping("/import-template")
    public void importTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        excelService.exportTemplate(response.getOutputStream(), SysUser.class, "用户数据", "用户导入模板");
    }

    @PreAuthorize("@se.hasPermission('system:user:query')")
    @GetMapping({"/detail", "/detail/{id}"})
    public ApiResult<UserDetailVO> getInfo(@PathVariable(required = false) Long id) {
        SysUser user = null;
        List<Long> roleIds = List.of();
        List<Long> postIds = List.of();
        if (id != null) {
            userService.checkUserDataScope(id);
            user = userService.selectUserById(id);
            roleIds = roleService.selectRoleListByUserId(id);
            postIds = postService.selectPostListByUserId(id);
        }
        List<SysRole> roles = roleService.selectRoleAll().stream()
                .filter(role -> (id != null && SecurityUtils.isAdmin(id)) || !role.isAdmin())
                .toList();
        return success(new UserDetailVO(user, roles, postService.selectPostAll(), roleIds, postIds));
    }

    @PreAuthorize("@se.hasPermission('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody UserSaveDTO request) {
        SysUser user = BeanUtil.toBean(request, SysUser.class);
        if (StrUtil.isBlank(user.getPassword())) {
            return error("新增用户失败，初始密码不能为空");
        }
        checkRelatedDataScope(user);
        ApiResult<Void> validation = validateUser(user, "新增");
        if (validation != null) {
            return validation;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toApiResult(userService.insertUser(user));
    }

    @PreAuthorize("@se.hasPermission('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody UserSaveDTO request) {
        SysUser user = BeanUtil.toBean(request, SysUser.class);
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        checkRelatedDataScope(user);
        ApiResult<Void> validation = validateUser(user, "修改");
        return validation == null ? toApiResult(userService.updateUser(user)) : validation;
    }

    @PreAuthorize("@se.hasPermission('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public ApiResult<Void> remove(@PathVariable Long[] ids) {
        if (Arrays.asList(ids).contains(SecurityUtils.getUserId())) {
            return error("不能删除当前登录用户");
        }
        return toApiResult(userService.deleteUserByIds(ids));
    }

    @PreAuthorize("@se.hasPermission('system:user:reset-password')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    public ApiResult<Void> resetPassword(@Validated @RequestBody PasswordResetDTO request) {
        SysUser user = BeanUtil.toBean(request, SysUser.class);
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toApiResult(userService.resetPwd(user));
    }

    @PreAuthorize("@se.hasPermission('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public ApiResult<Void> changeStatus(@Validated @RequestBody StatusUpdateDTO request) {
        SysUser user = BeanUtil.toBean(request, SysUser.class);
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        return toApiResult(userService.updateUserStatus(user));
    }

    @PreAuthorize("@se.hasPermission('system:user:query')")
    @GetMapping("/{id}/roles")
    public ApiResult<UserRoleGrantVO> getUserRoles(@PathVariable Long id) {
        userService.checkUserDataScope(id);
        List<SysRole> roles = roleService.selectRolesByUserId(id).stream()
                .filter(role -> SecurityUtils.isAdmin(id) || !role.isAdmin())
                .toList();
        return success(new UserRoleGrantVO(userService.selectUserById(id), roles));
    }

    @PreAuthorize("@se.hasPermission('system:user:edit')")
    @Log(title = "用户角色授权", businessType = BusinessType.GRANT)
    @PutMapping("/{id}/roles")
    public ApiResult<Void> updateUserRoles(@PathVariable Long id, @Validated @RequestBody RoleIdsDTO request) {
        userService.checkUserDataScope(id);
        roleService.checkRoleDataScope(request.getRoleIds());
        userService.insertUserAuth(id, request.getRoleIds());
        return success();
    }

    @PreAuthorize("@se.hasPermission('system:user:list')")
    @GetMapping("/dept-tree")
    public ApiResult<List<TreeSelect>> deptTree(SysDept query) {
        return success(deptService.selectDeptTreeList(query));
    }

    private void checkRelatedDataScope(SysUser user) {
        if (user.getDeptId() != null) {
            deptService.checkDeptDataScope(user.getDeptId());
        }
        if (user.getRoleIds() != null && user.getRoleIds().length > 0) {
            roleService.checkRoleDataScope(user.getRoleIds());
        }
    }

    private ApiResult<Void> validateUser(SysUser user, String action) {
        if (!userService.checkUserNameUnique(user)) {
            return error(action + "用户失败，登录账号已存在：" + user.getUserName());
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank() && !userService.checkPhoneUnique(user)) {
            return error(action + "用户失败，手机号码已存在");
        }
        if (user.getEmail() != null && !user.getEmail().isBlank() && !userService.checkEmailUnique(user)) {
            return error(action + "用户失败，邮箱已存在");
        }
        return null;
    }
}
