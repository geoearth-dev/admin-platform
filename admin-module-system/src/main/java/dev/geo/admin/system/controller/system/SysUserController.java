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
import dev.geo.admin.system.model.system.dto.*;
import dev.geo.admin.system.model.system.entity.SysDept;
import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.model.system.vo.TreeSelect;
import dev.geo.admin.system.service.system.ISysDeptService;
import dev.geo.admin.system.service.system.ISysPostService;
import dev.geo.admin.system.service.system.ISysRoleService;
import dev.geo.admin.system.service.system.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "用户管理")
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
    @Operation(summary = "分页查询用户")
    public ApiResult<PageResult<SysUser>> list(@Validated @ParameterObject UserPageReqDTO query) {
        return success(userService.selectUserPage(query));
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:user:export')")
    @PostMapping("/export")
    @Operation(summary = "导出用户")
    public void export(HttpServletResponse response, @Validated @ParameterObject UserPageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, userService.selectUserPage(query).getRecords(), SysUser.class, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@se.hasPermission('system:user:import')")
    @PostMapping(value = "/import", consumes = "multipart/form-data")
    @Operation(summary = "导入用户", description = "上传用户 Excel；updateSupport 控制是否更新已存在的用户。")
    public ApiResult<Void> importData(@Parameter(description = "待上传的文件") @RequestPart MultipartFile file,
                                      @Parameter(description = "是否更新已存在的用户，默认 false") @RequestParam(defaultValue = "false") boolean updateSupport) throws IOException {
        List<SysUser> users = excelService.importExcel(file.getInputStream(), SysUser.class);
        return success(userService.importUser(users, updateSupport, SecurityUtils.getUsername()));
    }

    @GetMapping("/import-template")
    @Operation(summary = "下载用户导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        excelService.exportTemplate(response.getOutputStream(), SysUser.class, "用户数据", "用户导入模板");
    }

    @PreAuthorize("@se.hasPermission('system:user:query')")
    @GetMapping({"/detail", "/detail/{id}"})
    @Operation(summary = "获取用户编辑信息", description = "返回用户资料、可选角色和已选角色及岗位 ID；不传 id 时 data 为空。")
    public ApiResult<SysUser> getInfo(@Parameter(description = "用户 ID") @PathVariable(required = false) Long id) {
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
                .filter(role -> (SecurityUtils.isAdmin(id)) || !role.isAdmin())
                .toList();
//        List<SysPost> posts =   postService.selectPostAll();
        if (user != null) {
            user.setRoles(roles);
            user.setRoleIds(roleIds.toArray(Long[]::new));
//            user.setPosts(posts);
            user.setPostIds(postIds.toArray(Long[]::new));

        }
        return success(user);
    }

    @PreAuthorize("@se.hasPermission('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增用户")
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
    @Operation(summary = "修改用户")
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
    @Operation(summary = "删除用户")
    public ApiResult<Void> remove(@Parameter(description = "用户 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        if (Arrays.asList(ids).contains(SecurityUtils.getUserId())) {
            return error("不能删除当前登录用户");
        }
        return toApiResult(userService.deleteUserByIds(ids));
    }

    @PreAuthorize("@se.hasPermission('system:user:reset-password')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    @Operation(summary = "重置用户密码")
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
    @Operation(summary = "修改用户状态")
    public ApiResult<Void> changeStatus(@Validated @RequestBody StatusUpdateDTO request) {
        SysUser user = BeanUtil.toBean(request, SysUser.class);
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        return toApiResult(userService.updateUserStatus(user));
    }

    @PreAuthorize("@se.hasPermission('system:user:query')")
    @GetMapping("/{id}/roles")
    @Operation(summary = "查询用户角色")
    public ApiResult<List<SysRole>> getUserRoles(@Parameter(description = "用户 ID") @PathVariable Long id) {
        userService.checkUserDataScope(id);
        List<SysRole> roles = roleService.selectRolesByUserId(id).stream()
                .filter(role -> SecurityUtils.isAdmin(id) || !role.isAdmin())
                .toList();
        return success(roles);
    }

    @PreAuthorize("@se.hasPermission('system:user:edit')")
    @Log(title = "用户角色授权", businessType = BusinessType.GRANT)
    @PutMapping("/{id}/roles")
    @Operation(summary = "设置用户角色")
    public ApiResult<Void> updateUserRoles(@Parameter(description = "用户 ID") @PathVariable Long id, @Validated @RequestBody Long[] roleIds) {
        userService.checkUserDataScope(id);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(id, roleIds);
        return success();
    }

    @PreAuthorize("@se.hasPermission('system:user:list')")
    @GetMapping("/dept-tree")
    @Operation(summary = "查询部门树选项")
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
