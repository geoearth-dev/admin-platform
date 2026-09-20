package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.excel.core.ExcelService;
import dev.geo.admin.mybatis.model.page.PageParam;
import dev.geo.admin.system.model.system.entity.SysDept;
import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.entity.SysUser;
import dev.geo.admin.system.model.system.entity.SysUserRole;
import dev.geo.admin.system.model.system.dto.RoleDataScopeDTO;
import dev.geo.admin.system.model.system.dto.RoleSaveDTO;
import dev.geo.admin.system.model.system.dto.StatusUpdateDTO;
import dev.geo.admin.system.model.system.dto.RoleUserRelationDTO;
import dev.geo.admin.system.model.system.dto.RolePageReqDTO;
import dev.geo.admin.system.model.system.dto.RoleUserPageReqDTO;
import dev.geo.admin.system.model.system.vo.RoleDeptTreeVO;
import dev.geo.admin.system.service.system.ISysDeptService;
import dev.geo.admin.system.service.system.ISysRoleService;
import dev.geo.admin.system.service.system.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口。
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController extends BaseController {
    private final ISysRoleService roleService;
    private final ISysUserService userService;
    private final ISysDeptService deptService;
    private final ExcelService excelService;

    @PreAuthorize("@se.hasPermission('system:role:list')")
    @GetMapping("/list")
    @Operation(summary = "分页查询角色")
    public ApiResult<PageResult<SysRole>> list(@Validated @ParameterObject RolePageReqDTO query) {
        return success(roleService.selectRolePage(query));
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@se.hasPermission('system:role:export')")
    @PostMapping("/export")
    @Operation(summary = "导出角色")
    public void export(HttpServletResponse response, @Validated @ParameterObject RolePageReqDTO query) {
        query.setPageSize(PageParam.PAGE_SIZE_NONE);
        excelService.exportExcel(response, roleService.selectRolePage(query).getRecords(), SysRole.class, "角色数据");
    }
    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@se.hasPermission('system:role:query')")
    @GetMapping("/{id}")
    @Operation(summary = "查询角色详情")
    public ApiResult<SysRole> getRole(@Parameter(description = "角色 ID") @PathVariable Long id) {
        roleService.checkRoleDataScope(id);
        return success(roleService.selectRoleById(id));
    }
    /**
     * 新增角色
     */
    @PostMapping
    @PreAuthorize("@se.hasPermission('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @Operation(summary = "新增角色")
    public ApiResult<Void> add(@Validated @RequestBody RoleSaveDTO request) {
        SysRole role = BeanUtil.toBean(request, SysRole.class);
        ApiResult<Void> validation = validateRole(role, "新增");
        return validation == null ? toApiResult(roleService.insertRole(role)) : validation;
    }
    /**
     * 修改保存角色
     */
    @PutMapping
    @PreAuthorize("@se.hasPermission('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "修改角色")
    public ApiResult<Void> edit(@Validated @RequestBody RoleSaveDTO request) {
        SysRole role = BeanUtil.toBean(request, SysRole.class);
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getId());
        ApiResult<Void> validation = validateRole(role, "修改");
        return validation == null ? toApiResult(roleService.updateRole(role)) : validation;
    }
    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@se.hasPermission('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/data-scope")
    @Operation(summary = "设置角色数据权限")
    public ApiResult<Void> dataScope(@Validated @RequestBody RoleDataScopeDTO request) {
        SysRole role = BeanUtil.toBean(request, SysRole.class);
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getId());
        return toApiResult(roleService.authDataScope(role));
    }
    /**
     * 状态修改
     */
    @PreAuthorize("@se.hasPermission('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    @Operation(summary = "修改角色状态")
    public ApiResult<Void> changeStatus(@Validated @RequestBody StatusUpdateDTO request) {
        SysRole role = BeanUtil.toBean(request, SysRole.class);
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getId());
        return toApiResult(roleService.updateRoleStatus(role));
    }
    /**
     * 删除角色
     */
    @PreAuthorize("@se.hasPermission('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除角色")
    public ApiResult<Void> remove(@Parameter(description = "角色 ID 列表，多个用逗号分隔") @PathVariable Long[] ids) {
        return toApiResult(roleService.deleteRoleByIds(ids));
    }
    /**
     * 获取角色选择框列表
     */
    @GetMapping("/options")
    @Operation(summary = "查询角色选项")
    public ApiResult<List<SysRole>> options() {
        return success(roleService.selectRoleAll());
    }


    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@se.hasPermission('system:role:list')")
    @GetMapping("/users/allocated")
    @Operation(summary = "分页查询角色已分配用户")
    public ApiResult<PageResult<SysUser>> allocatedUsers(@Validated @ParameterObject RoleUserPageReqDTO query) {
        return success(userService.selectAllocatedPage(query));
    }
    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@se.hasPermission('system:role:list')")
    @GetMapping("/users/unallocated")
    @Operation(summary = "分页查询角色未分配用户")
    public ApiResult<PageResult<SysUser>> unallocatedUsers(@Validated @ParameterObject RoleUserPageReqDTO query) {
        return success(userService.selectUnallocatedPage(query));
    }
    /**
     * 取消授权用户
     */
    @PreAuthorize("@se.hasPermission('system:role:edit')")
    @Log(title = "角色授权", businessType = BusinessType.GRANT)
    @DeleteMapping("/users")
    @Operation(summary = "取消用户的角色授权")
    public ApiResult<Void> cancelUser(@Validated @RequestBody RoleUserRelationDTO request) {
        SysUserRole userRole = BeanUtil.toBean(request, SysUserRole.class);
        return toApiResult(roleService.deleteAuthUser(userRole));
    }
    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@se.hasPermission('system:role:edit')")
    @Log(title = "角色授权", businessType = BusinessType.GRANT)
    @DeleteMapping("/{roleId}/users")
    @Operation(summary = "批量取消用户的角色授权")
    public ApiResult<Void> cancelUsers(@Parameter(description = "角色 ID") @PathVariable Long roleId, @Validated @RequestBody Long[] userIds) {
        return toApiResult(roleService.deleteAuthUsers(roleId, userIds));
    }
    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@se.hasPermission('system:role:edit')")
    @Log(title = "角色授权", businessType = BusinessType.GRANT)
    @PostMapping("/{roleId}/users")
    @Operation(summary = "批量为用户分配角色")
    public ApiResult<Void> selectUsers(@Parameter(description = "角色 ID") @PathVariable Long roleId, @Validated @RequestBody Long[] userIds) {
        roleService.checkRoleDataScope(roleId);
        return toApiResult(roleService.insertAuthUsers(roleId, userIds));
    }
    /**
     * 获取对应角色部门树列表
     */
    @PreAuthorize("@se.hasPermission('system:role:query')")
    @GetMapping("/dept-tree/{roleId}")
    @Operation(summary = "查询角色部门树及已选项")
    public ApiResult<RoleDeptTreeVO> deptTree(@Parameter(description = "角色 ID") @PathVariable Long roleId) {
        List<SysDept> departments = deptService.selectDeptList(new SysDept());
        return success(new RoleDeptTreeVO(
                deptService.selectDeptListByRoleId(roleId),
                deptService.buildDeptTreeSelect(departments)
        ));
    }

    private ApiResult<Void> validateRole(SysRole role, String action) {
        if (!roleService.checkRoleNameUnique(role)) {
            return error(action + "角色失败，角色名称已存在：" + role.getRoleName());
        }
        if (!roleService.checkRoleKeyUnique(role)) {
            return error(action + "角色失败，权限标识已存在：" + role.getRoleKey());
        }
        return null;
    }


}
