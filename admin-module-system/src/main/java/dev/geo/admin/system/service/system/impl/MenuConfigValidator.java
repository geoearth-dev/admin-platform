package dev.geo.admin.system.service.system.impl;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.system.model.system.entity.SysMenu;

import java.net.URI;
import java.util.*;

/**
 * 菜单保存约束；同时用于新增、编辑和路由冲突检查。
 */
public final class MenuConfigValidator {
    private MenuConfigValidator() {
    }

    public static void validate(SysMenu menu, List<SysMenu> existing) {
        if (!Set.of("catalog", "menu", "embedded", "link", "button").contains(Objects.toString(menu.getMenuType(), ""))) {
            throw new ServiceException("菜单类型只能为目录、菜单、内嵌、外链或按钮");
        }
        menu.setMenuName(trim(menu.getMenuName()));
        if (existing.stream().anyMatch(node -> !Objects.equals(node.getId(), menu.getId())
                && Objects.equals(node.getParentId(), menu.getParentId())
                && Objects.equals(trim(node.getMenuName()), menu.getMenuName()))) {
            throw new ServiceException("同级菜单名称已存在");
        }
        menu.setRouteName(trim(menu.getRouteName()));
        if (Set.of("Index", "Authentication", "Login", "FallbackNotFound").contains(StrUtil.upperFirst(Objects.toString(menu.getRouteName(), "")))) {
            throw new ServiceException("该路由名称由系统保留");
        }
        menu.setComponent(trim(menu.getComponent()));
        menu.setIcon(trim(menu.getIcon()));
        menu.setPerms(trim(menu.getPerms()));
        menu.setLink(trim(menu.getLink()));
        menu.setIframeSrc(trim(menu.getIframeSrc()));
        menu.setActivePath(trim(menu.getActivePath()));
        menu.setActiveIcon(trim(menu.getActiveIcon()));
        menu.setBadge(trim(menu.getBadge()));
        menu.setBadgeType(trim(menu.getBadgeType()));
        menu.setBadgeVariants(trim(menu.getBadgeVariants()));
        menu.setRedirect(trim(menu.getRedirect()));

        Map<Long, SysMenu> nodes = index(existing);
        Long id = menu.getId();
        Set<Long> parents = new HashSet<>();
        Long parentId = menu.getParentId();
        while (parentId != null && parentId != 0) {
            if (Objects.equals(parentId, id) || !parents.add(parentId)) {
                throw new ServiceException("上级菜单不能是自身或下级菜单");
            }
            SysMenu parent = nodes.get(parentId);
            if (parent == null) throw new ServiceException("上级菜单不存在");
            if (Set.of("button", "link", "embedded").contains(parent.getMenuType())) {
                throw new ServiceException("按钮、外链和内嵌页面不能作为上级菜单");
            }
            parentId = parent.getParentId();
        }
        boolean button = "button".equals(menu.getMenuType());
        boolean internal = "menu".equals(menu.getMenuType());
        boolean page = internal || "embedded".equals(menu.getMenuType()) || "link".equals(menu.getMenuType());
        boolean external = "embedded".equals(menu.getMenuType()) || "link".equals(menu.getMenuType());
        if ("link".equals(menu.getMenuType()) && menu.getLink() == null)
            throw new ServiceException("外链必须填写链接地址");
        if ("embedded".equals(menu.getMenuType()) && menu.getIframeSrc() == null)
            throw new ServiceException("内嵌页面必须填写链接地址");
        if (!"link".equals(menu.getMenuType())) menu.setLink(null);
        if (!"embedded".equals(menu.getMenuType())) menu.setIframeSrc(null);
        boolean hasChildren = existing.stream().anyMatch(node -> Objects.equals(node.getParentId(), id));
        if ((button || external) && hasChildren) throw new ServiceException("存在下级菜单，不能改为按钮、外链或内嵌页面");
        if (button && (menu.getParentId() == null || menu.getParentId() == 0)) {
            throw new ServiceException("按钮必须归属于上级菜单");
        }
        if (button && menu.getPerms() == null) throw new ServiceException("按钮必须填写权限标识");
        checkUrl(menu.getLink());
        checkUrl(menu.getIframeSrc());
        if (!button) {
            String path = trim(menu.getPath());
            if (path == null || path.contains("://") || path.contains("?") || path.contains("#")
                    || path.contains("\\") || path.contains(" ")) {
                throw new ServiceException("路由地址必须是内部路径，查询参数请单独配置");
            }
            if (Arrays.asList(path.split("/")).contains("..") || Arrays.asList(path.split("/")).contains(".")) {
                throw new ServiceException("路由路径不能包含 . 或 .. 路径段");
            }
            menu.setPath(normalizePath(path));
            String resolvedPath = fullPath(menu, nodes, new HashSet<>());
            if (resolvedPath.equals("/") || resolvedPath.equals("/auth") || resolvedPath.startsWith("/auth/")) {
                throw new ServiceException("该路由路径由系统保留");
            }
            if (page && !external && menu.getComponent() == null)
                throw new ServiceException("普通菜单必须选择页面组件");
            if (menu.getComponent() != null) {
                menu.setComponent(menu.getComponent().replaceAll("^/+", "").replaceAll("\\.vue$", ""));
            }
            if (!isRouteUnique(menu, existing)) throw new ServiceException("路由名称或完整路径已存在");
        }
        if (menu.getQuery() != null) {
            for (Object value : menu.getQuery().values()) {
                if (!isQueryValue(value))
                    throw new ServiceException("路由参数只能包含字符串、数字、null或这些值组成的数组");
            }
        }
        if (menu.getBadgeType() != null && !Set.of("dot", "normal").contains(menu.getBadgeType())) {
            throw new ServiceException("徽标类型只能为dot或normal");
        }
        if (menu.getBadgeVariants() != null && !Set.of("default", "destructive", "primary", "success", "warning").contains(menu.getBadgeVariants())) {
            throw new ServiceException("徽标样式不支持");
        }
        checkInternalPath(menu.getActivePath(), "激活路径");
        checkInternalPath(menu.getRedirect(), "重定向路径");
        if (Boolean.TRUE.equals(menu.getNoBasicLayout()) && menu.getParentId() != 0) {
            throw new ServiceException("不使用基础布局仅支持顶级页面");
        }
        if (menu.getMaxNumOfOpenTab() != null && (menu.getMaxNumOfOpenTab() == 0 || menu.getMaxNumOfOpenTab() < -1)) {
            throw new ServiceException("最多标签数只能为-1或正整数");
        }
        if (!"normal".equals(menu.getBadgeType())) menu.setBadge(null);
        if (menu.getBadgeType() == null) menu.setBadgeVariants(null);

        // 内嵌、外链等类型不使用页面组件
        if (!page || external) {
            menu.setComponent(null);
        }
        // 只有普通菜单和内嵌页面支持缓存
        if (!internal && !"embedded".equals(menu.getMenuType())) {
            menu.setKeepAlive(false);
        }
        if (!page || menu.getLink() != null) {
            menu.setAffixTab(false);
            menu.setHideInTab(false);
            menu.setNoBasicLayout(false);
            menu.setOpenInNewWindow(false);
            menu.setMaxNumOfOpenTab(-1);
            menu.setActivePath(null);
        }
        if (button) {
            menu.setPath("");
            menu.setRouteName(null);
            menu.setIcon(null);
            menu.setActiveIcon(null);
            menu.setBadge(null);
            menu.setBadgeType(null);
            menu.setBadgeVariants(null);
            menu.setHideInMenu(false);
            menu.setHideInBreadcrumb(false);
            menu.setQuery(null);
        }
        if (button || external) {
            menu.setRedirect(null);
            menu.setHideChildrenInMenu(false);
        }
        if (!Boolean.TRUE.equals(menu.getAffixTab())) menu.setAffixTabOrder(0);
        if (Boolean.TRUE.equals(menu.getAffixTab()) && Boolean.TRUE.equals(menu.getHideInTab())) {
            throw new ServiceException("固定标签和隐藏标签不能同时启用");
        }
        if (menu.getRedirect() != null) {
            nodes.put(id == null ? Long.MIN_VALUE : id, menu);
            String ownPath = fullPath(menu, nodes, new HashSet<>());
            if (ownPath.equals(normalizePath(menu.getRedirect()))) throw new ServiceException("重定向不能指向自身");
            Map<String, String> redirects = new HashMap<>();
            for (SysMenu node : nodes.values()) {
                if (!"button".equals(node.getMenuType()) && StrUtil.isNotBlank(node.getRedirect())) {
                    redirects.put(fullPath(node, nodes, new HashSet<>()), normalizePath(node.getRedirect()));
                }
            }
            Set<String> visited = new HashSet<>();
            String target = ownPath;
            while (target != null) {
                if (!visited.add(target)) throw new ServiceException("重定向配置形成循环");
                target = redirects.get(target);
            }
        }
    }

    public static boolean isRouteUnique(SysMenu candidate, List<SysMenu> existing) {
        Map<Long, SysMenu> nodes = index(existing);
        Long key = candidate.getId() == null ? Long.MIN_VALUE : candidate.getId();
        nodes.put(key, candidate);
        Map<String, Long> names = new HashMap<>();
        Map<String, Long> paths = new HashMap<>();
        for (var entry : nodes.entrySet()) {
            SysMenu node = entry.getValue();
            if ("button".equals(node.getMenuType())) continue;
            String name = StrUtil.upperFirst(StrUtil.isNotBlank(node.getRouteName()) ? node.getRouteName().trim() : Objects.toString(node.getPath(), ""));
            String path = fullPath(node, nodes, new HashSet<>());
            if (names.putIfAbsent(name, entry.getKey()) != null || paths.putIfAbsent(path, entry.getKey()) != null)
                return false;
        }
        return true;
    }

    private static Map<Long, SysMenu> index(List<SysMenu> menus) {
        Map<Long, SysMenu> nodes = new LinkedHashMap<>();
        for (SysMenu menu : menus) nodes.put(menu.getId(), menu);
        return nodes;
    }

    private static String fullPath(SysMenu menu, Map<Long, SysMenu> nodes, Set<Long> visited) {
        if (menu.getId() != null && !visited.add(menu.getId())) throw new ServiceException("菜单层级存在循环");
        String path = Objects.toString(menu.getPath(), "");
        if (path.startsWith("/")) return normalizePath(path);
        SysMenu parent = nodes.get(menu.getParentId());
        return normalizePath((parent == null ? "" : fullPath(parent, nodes, visited)) + "/" + path);
    }

    private static String normalizePath(String path) {
        String result = path.replaceAll("/{2,}", "/");
        return result.length() > 1 ? result.replaceAll("/+$", "") : result;
    }

    private static void checkInternalPath(String value, String label) {
        if (value != null && (!value.startsWith("/") || value.startsWith("//") || value.contains("://"))) {
            throw new ServiceException(label + "必须是以 / 开头的内部完整路径");
        }
    }

    private static void checkUrl(String value) {
        if (value == null) return;
        try {
            URI uri = URI.create(value);
            if (("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) && uri.getHost() != null)
                return;
        } catch (IllegalArgumentException ignored) {
            // 统一抛出业务提示。
        }
        throw new ServiceException("链接必须是有效的HTTP或HTTPS网址");
    }

    private static boolean isQueryValue(Object value) {
        if (value == null || value instanceof String || value instanceof Number) return true;
        return value instanceof List<?> list && list.stream().allMatch(item -> item == null || item instanceof String || item instanceof Number);
    }

    private static String trim(String value) {
        return StrUtil.trimToNull(value);
    }
}

