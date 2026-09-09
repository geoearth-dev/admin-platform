package dev.geo.admin.system.controller.system;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import dev.geo.admin.common.annotation.Log;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.core.controller.BaseController;
import dev.geo.admin.common.core.model.ApiResult;
import dev.geo.admin.common.enums.BusinessType;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.model.system.entity.SysMenu;
import dev.geo.admin.system.model.system.dto.MenuSaveDTO;
import dev.geo.admin.system.model.system.vo.RoleMenuTreeVO;
import dev.geo.admin.system.model.system.vo.TreeSelect;
import dev.geo.admin.system.service.system.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 菜单管理接口。
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {
    private final ISysMenuService menuService;

    @PreAuthorize("@se.hasPermission('system:menu:list')")
    @GetMapping("/list")
    public ApiResult<List<SysMenu>> list(SysMenu query) {
        return success(menuService.selectMenuList(query, SecurityUtils.getUserId()));
    }

    @PreAuthorize("@se.hasPermission('system:menu:query')")
    @GetMapping("/{id}")
    public ApiResult<SysMenu> getInfo(@PathVariable Long id) {
        return success(menuService.selectMenuById(id));
    }

    @GetMapping("/tree-select")
    public ApiResult<List<TreeSelect>> treeSelect(SysMenu query) {
        List<SysMenu> menus = menuService.selectMenuList(query, SecurityUtils.getUserId());
        return success(menuService.buildMenuTreeSelect(menus));
    }

    @GetMapping("/role-tree/{roleId}")
    public ApiResult<RoleMenuTreeVO> roleTree(@PathVariable Long roleId) {
        List<SysMenu> menus = menuService.selectMenuList(SecurityUtils.getUserId());
        RoleMenuTreeVO result = new RoleMenuTreeVO(
                menuService.selectMenuListByRoleId(roleId),
                menuService.buildMenuTreeSelect(menus)
        );
        return success(result);
    }

    @PreAuthorize("@se.hasPermission('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public ApiResult<Void> add(@Validated @RequestBody MenuSaveDTO request) {
        SysMenu menu = BeanUtil.toBean(request, SysMenu.class);
        ApiResult<Void> validation = validateMenu(menu, "新增");
        return validation == null ? toApiResult(menuService.insertMenu(menu)) : validation;
    }

    @PreAuthorize("@se.hasPermission('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public ApiResult<Void> edit(@Validated @RequestBody MenuSaveDTO request) {
        SysMenu menu = BeanUtil.toBean(request, SysMenu.class);
        ApiResult<Void> validation = validateMenu(menu, "修改");
        if (validation != null) {
            return validation;
        }
        if (Objects.equals(menu.getId(), menu.getParentId())) {
            return error("上级菜单不能是当前菜单自身");
        }
        return toApiResult(menuService.updateMenu(menu));
    }

    @PreAuthorize("@se.hasPermission('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public ApiResult<Void> remove(@PathVariable Long id) {
        if (menuService.hasChildByMenuId(id)) {
            return warn("当前菜单存在子菜单，不能删除");
        }
        if (menuService.checkMenuExistRole(id)) {
            return warn("当前菜单已分配给角色，不能删除");
        }
        return toApiResult(menuService.deleteMenuById(id));
    }

    private ApiResult<Void> validateMenu(SysMenu menu, String action) {
        menu.setLink(StrUtil.trimToNull(menu.getLink()));
        menu.setIframeSrc(StrUtil.trimToNull(menu.getIframeSrc()));
        menu.setQuery(StrUtil.trimToNull(menu.getQuery()));
        if (menu.getLink() != null && menu.getIframeSrc() != null) {
            return error("外链地址和iframe地址不能同时填写");
        }
        if ((menu.getLink() != null || menu.getIframeSrc() != null)
                && !UserConstants.TYPE_MENU.equals(menu.getMenuType())) {
            return error("只有菜单页面可以配置外链或iframe地址");
        }
        if (menu.getLink() != null
                && !StrUtil.startWithAnyIgnoreCase(menu.getLink(), "http://", "https://")) {
            return error("外链地址必须以 http:// 或 https:// 开头");
        }
        if (menu.getIframeSrc() != null
                && !StrUtil.startWithAnyIgnoreCase(menu.getIframeSrc(), "http://", "https://")) {
            return error("iframe地址必须以 http:// 或 https:// 开头");
        }
        if (menu.getQuery() != null && !JSON.isValidObject(menu.getQuery())) {
            return error("路由参数必须是JSON对象");
        }
        if (!UserConstants.TYPE_BUTTON.equals(menu.getMenuType())) {
            if (StrUtil.isBlank(menu.getPath())
                    || StrUtil.startWithAnyIgnoreCase(menu.getPath(), "http://", "https://")) {
                return error("路由地址必须填写系统内部路径，外部网址请填写到外链或iframe地址");
            }
            if (UserConstants.TYPE_MENU.equals(menu.getMenuType())
                    && menu.getLink() == null && menu.getIframeSrc() == null
                    && StrUtil.isBlank(menu.getComponent())) {
                return error("普通菜单页面必须填写组件路径");
            }
        }
        if (!menuService.checkMenuNameUnique(menu)) {
            return error(action + "菜单失败，菜单名称已存在：" + menu.getMenuName());
        }
        if (!UserConstants.TYPE_BUTTON.equals(menu.getMenuType())
                && !menuService.checkRouteConfigUnique(menu)) {
            return error(action + "菜单失败，路由名称或路由地址已存在");
        }
        return null;
    }
}
