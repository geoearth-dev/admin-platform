package dev.geo.admin.system.service.system.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.security.utils.SecurityUtils;
import dev.geo.admin.system.mapper.system.SysMenuMapper;
import dev.geo.admin.system.mapper.system.SysRoleMapper;
import dev.geo.admin.system.mapper.system.SysRoleMenuMapper;
import dev.geo.admin.system.model.system.entity.SysMenu;
import dev.geo.admin.system.model.system.entity.SysRole;
import dev.geo.admin.system.model.system.vo.MetaVo;
import dev.geo.admin.system.model.system.vo.RouterVo;
import dev.geo.admin.system.model.system.vo.TreeSelect;
import dev.geo.admin.system.service.system.ISysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {
    private static final Logger log = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    public static final Long MENU_ROOT_ID = 0L;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId) {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId) {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SecurityUtils.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StrUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StrUtil.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId)) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, MENU_ROOT_ID);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        SysRole role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckLinked());
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : menus) {
            // 按钮只参与权限码计算，不生成页面路由。
            if (UserConstants.TYPE_BUTTON.equals(menu.getMenuType())) {
                continue;
            }
            RouterVo router = new RouterVo();
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setRedirect(StrUtil.trimToNull(menu.getRedirect()));
            MetaVo meta = new MetaVo();
            meta.setTitle(menu.getMenuName());
            meta.setIcon(menu.getIcon());
            meta.setOrder(menu.getOrder());
            meta.setHideInMenu(Boolean.TRUE.equals(menu.getHideInMenu()));
            meta.setKeepAlive(Boolean.TRUE.equals(menu.getKeepAlive()));
            meta.setLink(StrUtil.trimToNull(menu.getLink()));
            meta.setIframeSrc(StrUtil.trimToNull(menu.getIframeSrc()));
            meta.setQuery(menu.getQuery());
            meta.setActiveIcon(menu.getActiveIcon());
            meta.setActivePath(menu.getActivePath());
            meta.setAffixTab(menu.getAffixTab());
            meta.setAffixTabOrder(menu.getAffixTabOrder());
            meta.setBadge(menu.getBadge());
            meta.setBadgeType(menu.getBadgeType());
            meta.setBadgeVariants(menu.getBadgeVariants());
            meta.setHideChildrenInMenu(menu.getHideChildrenInMenu());
            meta.setHideInBreadcrumb(menu.getHideInBreadcrumb());
            meta.setHideInTab(menu.getHideInTab());
            meta.setOpenInNewWindow(menu.getOpenInNewWindow());
            meta.setNoBasicLayout(menu.getNoBasicLayout());
            meta.setMaxNumOfOpenTab(menu.getMaxNumOfOpenTab());
            router.setMeta(meta);
            if (ObjectUtil.isNotEmpty(menu.getChildren())) {
                router.setChildren(buildMenus(menu.getChildren()));
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getId).toList();
        for (SysMenu menu : menus) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu) {
        MenuConfigValidator.validate(menu, menuMapper.selectMenuList(new SysMenu()));
        menu.setId(null);
        return menuMapper.insertMenu(menu);
    }

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu) {
        if (menu.getId() == null || menuMapper.selectMenuById(menu.getId()) == null) {
            throw new ServiceException("菜单不存在，请刷新后重试");
        }
        MenuConfigValidator.validate(menu, menuMapper.selectMenuList(new SysMenu()));
        return menuMapper.updateMenu(menu);
    }

    /**
     * 保存菜单排序
     *
     * @param menuIds   菜单ID
     * @param orders 排序ID
     */
    @Override
    @Transactional
    public void updateMenuSort(String[] menuIds, String[] orders) {
        if (menuIds.length != orders.length) throw new ServiceException("菜单ID和排序数量不一致");
        Set<Long> ids = new HashSet<>();
        List<SysMenu> updates = new ArrayList<>();
        for (int i = 0; i < menuIds.length; i++) {
            Long id;
            Integer order;
            try {
                id = Long.valueOf(menuIds[i].trim());
                order = Integer.valueOf(orders[i].trim());
            } catch (NumberFormatException exception) {
                throw new ServiceException("菜单ID和排序必须是整数");
            }
            if (id <= 0 || order < 0 || !ids.add(id) || menuMapper.selectMenuById(id) == null) {
                throw new ServiceException("菜单不存在、ID重复或排序无效");
            }
            SysMenu menu = new SysMenu();
            menu.setId(id);
            menu.setOrder(order);
            updates.add(menu);
        }
        updates.forEach(menuMapper::updateMenuSort);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId) {
        return menuMapper.deleteMenuById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        long menuId = ObjectUtil.isNull(menu.getId()) ? -1L : menu.getId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (ObjectUtil.isNotNull(info) && info.getId() != menuId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验路由名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkRouteConfigUnique(SysMenu menu) {
        return MenuConfigValidator.isRouteUnique(menu, menuMapper.selectMenuList(new SysMenu()));
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        return getRouteName(menu.getRouteName(), menu.getPath());
    }

    /**
     * 获取路由名称，如没有配置路由名称则取路由地址
     *
     * @param name 路由名称
     * @param path 路由地址
     * @return 路由名称（驼峰格式）
     */
    public String getRouteName(String name, String path) {
        String routerName = StrUtil.isNotEmpty(name) ? name : path;
        return StrUtil.upperFirst(routerName);
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String path = menu.getPath();
        // Vben 顶级使用绝对路径；子级保留配置的相对路径或绝对路径。
        if (MENU_ROOT_ID.equals(menu.getParentId()) && !path.startsWith("/")) {
            return "/" + path;
        }
        return path;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        if (UserConstants.TYPE_DIR.equals(menu.getMenuType()) || UserConstants.TYPE_LINK.equals(menu.getMenuType())) {
            return null;
        }
        if (UserConstants.TYPE_EMBEDDED.equals(menu.getMenuType())) {
            return "IFrameView";
        }
        return menu.getComponent();
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, long parentId) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (SysMenu t : list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list 分类表
     * @param t    子节点
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        for (SysMenu n : list) {
            if (n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return !getChildList(list, t).isEmpty();
    }

}
