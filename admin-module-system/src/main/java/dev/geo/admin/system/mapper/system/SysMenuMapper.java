package dev.geo.admin.system.mapper.system;

import dev.geo.admin.mybatis.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import dev.geo.admin.system.model.system.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表 数据层
 *
 */
public interface SysMenuMapper extends BaseMapperX<SysMenu>
{
    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据用户所有权限
     *
     * @return 权限列表
     */
    List<String> selectMenuPerms();

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int hasChildByMenuId(Long menuId);

    /**
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    default int insertMenu(SysMenu menu) {
        return insert(menu);
    }

    /**
     * 修改菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    default int updateMenu(SysMenu menu) {
        // 切换打开方式时清空旧地址；普通 updateById 会忽略 null 字段。
        return update(menu, Wrappers.<SysMenu>lambdaUpdate()
                .eq(SysMenu::getId, menu.getId())
                .set(menu.getLink() == null, SysMenu::getLink, null)
                .set(menu.getIframeSrc() == null, SysMenu::getIframeSrc, null)
                .set(menu.getQuery() == null, SysMenu::getQuery, null)
                .set(menu.getComponent() == null, SysMenu::getComponent, null));
    }

    /**
     * 保存菜单排序
     * 
     * @param menu 菜单信息
     */
    default void updateMenuSort(SysMenu menu) {
        updateById(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    default int deleteMenuById(Long menuId) {
        return deleteById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);

    /**
     * 根据路由路径或名称查询菜单信息（用于唯一性校验）
     *
     * @param path 路由地址
     * @param routeName 路由名称
     * @return 匹配的菜单列表
     */
    List<SysMenu> selectMenusByPathOrRouteName(@Param("path") String path, @Param("routeName") String routeName);
}
