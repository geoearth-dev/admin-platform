package dev.geo.admin.system;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import dev.geo.admin.system.controller.system.SysMenuController;
import dev.geo.admin.system.mapper.system.SysMenuMapper;
import dev.geo.admin.system.model.system.dto.MenuSaveDTO;
import dev.geo.admin.system.model.system.entity.SysMenu;
import dev.geo.admin.system.service.system.ISysMenuService;
import dev.geo.admin.system.service.system.impl.SysMenuServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MenuRoutingTest {
    private final SysMenuServiceImpl service = new SysMenuServiceImpl();

    @Test
    void permissionAndRouteQueriesRequireEnabledMenusAndRoles() throws Exception {
        var configuration = new MybatisConfiguration();
        configuration.getTypeAliasRegistry().registerAlias("SysMenu", SysMenu.class);
        String resource = "mapper/system/SysMenuMapper.xml";
        try (var input = Resources.getResourceAsStream(resource)) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        for (String name : List.of("selectMenuTreeAll", "selectMenuTreeByUserId",
                "selectMenuPermsByUserId", "selectMenuPermsByRoleId")) {
            String sql = configuration.getMappedStatement(SysMenuMapper.class.getName() + "." + name)
                    .getBoundSql(java.util.Map.of("userId", 2L, "roleId", 2L)).getSql()
                    .replaceAll("\\s+", " ");
            assertTrue(sql.contains("m.status = '1'"), sql);
            assertFalse(sql.contains("status = '0'"), sql);
            if (name.equals("selectMenuTreeByUserId")) assertTrue(sql.contains("ro.status = '1'"), sql);
            if (name.equals("selectMenuPermsByUserId")) assertTrue(sql.contains("r.status = '1'"), sql);
        }
    }

    @Test
    void buildsVbenTreeAndSerializesMetadataWithNewBooleanSemantics() {
        SysMenu directory = menu(1L, 0L, "M", "system");
        directory.setComponent("Layout");
        SysMenu page = menu(2L, 1L, "C", "user");
        page.setMenuName("system.user.title");
        page.setComponent("system/user/index");
        page.setOrder(5);
        page.setHideInMenu(true);
        page.setKeepAlive(true);
        page.setQuery("{\"source\":\"menu\",\"page\":1}");
        directory.setChildren(List.of(page, menu(3L, 1L, "F", "")));

        var route = service.buildMenus(List.of(directory)).get(0);
        assertEquals("/system", route.getPath());
        assertNull(route.getComponent());
        assertNull(route.getRedirect());
        assertEquals(1, route.getChildren().size());
        var child = route.getChildren().get(0);
        assertEquals("user", child.getPath());
        assertEquals("system/user/index", child.getComponent());
        assertEquals("system.user.title", child.getMeta().getTitle());
        assertTrue(child.getMeta().isHideInMenu());
        assertTrue(child.getMeta().isKeepAlive());
        assertEquals(5, child.getMeta().getOrder());
        assertEquals(1, child.getMeta().getQuery().get("page"));

        var json = JsonMapper.builder().build().valueToTree(child);
        assertTrue(json.path("meta").path("keepAlive").asBoolean());
        assertTrue(json.path("meta").path("hideInMenu").asBoolean());
        assertTrue(json.path("meta").path("query").isObject());
        assertFalse(json.has("hidden"));
        assertFalse(json.has("alwaysShow"));
        assertFalse(json.has("query"));
        assertFalse(json.path("meta").has("noCache"));
    }

    @Test
    void distinguishesStandalonePageExternalLinkAndIframe() {
        SysMenu page = menu(1L, 0L, "C", "dashboard");
        page.setComponent("dashboard/analytics/index");
        SysMenu external = menu(2L, 0L, "C", "/external/docs");
        external.setLink("https://example.com/docs");
        SysMenu iframe = menu(3L, 0L, "C", "/external/report");
        iframe.setIframeSrc("https://example.com/report");

        var routes = service.buildMenus(List.of(page, external, iframe));
        assertEquals("/dashboard", routes.get(0).getPath());
        assertEquals("dashboard/analytics/index", routes.get(0).getComponent());
        assertNull(routes.get(0).getChildren());
        assertFalse(routes.get(0).getMeta().isHideInMenu());
        assertFalse(routes.get(0).getMeta().isKeepAlive());
        assertEquals("https://example.com/docs", routes.get(1).getMeta().getLink());
        assertNull(routes.get(1).getMeta().getIframeSrc());
        assertNull(routes.get(1).getComponent());
        assertEquals("IFrameView", routes.get(2).getComponent());
        assertEquals("https://example.com/report", routes.get(2).getMeta().getIframeSrc());
        assertNull(routes.get(2).getMeta().getLink());
    }

    @Test
    void rejectsConflictingAddressesAndInvalidQueryBeforeSaving() {
        var controller = new SysMenuController(null);
        var request = new MenuSaveDTO();
        request.setLink("https://example.com/docs");
        request.setIframeSrc("https://example.com/report");
        assertEquals("外链地址和iframe地址不能同时填写", controller.add(request).getMessage());

        request.setLink(null);
        request.setIframeSrc(null);
        request.setQuery("[1,2]");
        assertEquals("路由参数必须是JSON对象", controller.add(request).getMessage());
    }

    @Test
    void savesQueryAndNormalizesClearedAddresses() {
        var saved = new AtomicReference<SysMenu>();
        var menuService = (ISysMenuService) Proxy.newProxyInstance(
                ISysMenuService.class.getClassLoader(), new Class<?>[]{ISysMenuService.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "checkMenuNameUnique", "checkRouteConfigUnique" -> true;
                    case "updateMenu" -> {
                        saved.set((SysMenu) args[0]);
                        yield 1;
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        var request = new MenuSaveDTO();
        request.setId(1L);
        request.setMenuName("system.user.title");
        request.setOrder(5);
        request.setMenuType("C");
        request.setPath("/system/user");
        request.setComponent("system/user/index");
        request.setLink(" ");
        request.setIframeSrc("");
        request.setQuery("{\"source\":\"menu\"}");
        assertEquals(200, new SysMenuController(menuService).edit(request).getCode());
        assertNull(saved.get().getLink());
        assertNull(saved.get().getIframeSrc());
        assertEquals(request.getQuery(), saved.get().getQuery());
        assertEquals(5, saved.get().getOrder());
        assertEquals(Boolean.FALSE, saved.get().getKeepAlive());
        assertEquals(Boolean.FALSE, saved.get().getHideInMenu());
        assertEquals(1, saved.get().getStatus());
    }

    @Test
    void mapperUsesNewColumnsAndKeepsFalseAndZeroFilters() throws Exception {
        var configuration = new MybatisConfiguration();
        configuration.getTypeAliasRegistry().registerAlias("SysMenu", SysMenu.class);
        String resource = "mapper/system/SysMenuMapper.xml";
        try (var input = Resources.getResourceAsStream(resource)) {
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        var query = new SysMenu();
        query.setHideInMenu(false);
        query.setStatus(0);
        for (String statement : List.of("selectMenuList", "selectMenuListByUserId")) {
            String sql = configuration.getMappedStatement(SysMenuMapper.class.getName() + "." + statement)
                    .getBoundSql(query).getSql().replaceAll("\\s+", " ");
            assertTrue(sql.contains("hide_in_menu = ?"), sql);
            assertFalse(sql.contains("visible"), sql);
            assertTrue(sql.contains("status = ?"), sql);
            assertTrue(sql.contains("iframe_src"), sql);
            assertTrue(sql.contains("keep_alive"), sql);
            assertTrue(sql.contains("`order`"), sql);
            assertFalse(sql.contains("order_num"), sql);
            assertFalse(sql.contains("is_frame"), sql);
            assertFalse(sql.contains("is_cache"), sql);
        }
    }

    @Test
    void clearingAddressesGeneratesNullAssignmentsWithoutChangingSortUpdates() throws Throwable {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), "test"), SysMenu.class);
        var orderField = TableInfoHelper.getTableInfo(SysMenu.class).getFieldList().stream()
                .filter(field -> field.getProperty().equals("order")).findFirst().orElseThrow();
        assertEquals("`order`", orderField.getColumn());
        var captured = new AtomicReference<Wrapper<SysMenu>>();
        var mapper = (SysMenuMapper) Proxy.newProxyInstance(
                SysMenuMapper.class.getClassLoader(), new Class<?>[]{SysMenuMapper.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("update") && args.length == 2) {
                        captured.set((Wrapper<SysMenu>) args[1]);
                        return 1;
                    }
                    if (method.isDefault()) {
                        return InvocationHandler.invokeDefault(proxy, method, args);
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        var menu = menu(1L, 0L, "C", "/system/user");
        menu.setComponent("system/user/index");
        assertEquals(1, mapper.updateMenu(menu));
        String sqlSet = captured.get().getSqlSet();
        assertTrue(sqlSet.contains("link="), sqlSet);
        assertTrue(sqlSet.contains("iframe_src="), sqlSet);
        assertTrue(sqlSet.contains("`query`="), sqlSet);
        assertFalse(sqlSet.contains("component="), sqlSet);
        assertFalse(sqlSet.contains("keep_alive="), sqlSet);
    }

    private SysMenu menu(long id, long parentId, String type, String path) {
        SysMenu menu = new SysMenu();
        menu.setId(id);
        menu.setParentId(parentId);
        menu.setMenuType(type);
        menu.setPath(path);
        menu.setRouteName("Menu" + id);
        menu.setMenuName("menu." + id);
        menu.setOrder(0);
        menu.setHideInMenu(false);
        menu.setKeepAlive(false);
        menu.setStatus(1);
        return menu;
    }
}
