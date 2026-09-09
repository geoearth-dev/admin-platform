package dev.geo.admin.system.mapper.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.RoleUserPageReqDTO;
import dev.geo.admin.system.model.system.dto.UserPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

/**
 * 用户表 数据层
 */
public interface SysUserMapper extends BaseMapperX<SysUser> {
    IPage<SysUser> selectUserPage(Page<SysUser> page, @Param("query") UserPageReqDTO query);

    IPage<SysUser> selectAllocatedPage(Page<SysUser> page, @Param("query") RoleUserPageReqDTO query);

    IPage<SysUser> selectUnallocatedPage(Page<SysUser> page, @Param("query") RoleUserPageReqDTO query);

    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据条件分页查询已配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 根据条件分页查询未分配用户角色列表
     *
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUser selectUserById(Long userId);

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    default int insertUser(SysUser user) {
        return insert(user);
    }

    /**
     * 修改用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    default int updateUser(SysUser user) {
        return updateById(user);
    }

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    default int updateUserAvatar(Long userId, String avatar) {
        SysUser user = new SysUser(userId);
        user.setAvatar(avatar);
        return updateById(user);
    }

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 结果
     */
    default int updateUserStatus(Long userId, String status) {
        SysUser user = new SysUser(userId);
        user.setStatus(status);
        return updateById(user);
    }

    /**
     * 更新用户登录信息（IP和登录时间）
     *
     * @param userId        用户ID
     * @param lastLoginIp   登录IP地址
     * @param lastLoginTime 登录时间
     * @return 结果
     */
    int updateLoginLog(@Param("userId") Long userId, @Param("lastLoginIp") String lastLoginIp, @Param("lastLoginTime") Instant lastLoginTime);

    /**
     * 重置用户密码
     *
     * @param userId   用户ID
     * @param password 密码
     * @return 结果
     */
    default int resetUserPwd(Long userId, String password) {
        SysUser user = new SysUser(userId);
        user.setPassword(password);
        user.setPasswordUpdateTime(Instant.now());
        return updateById(user);
    }

    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    default int deleteUserById(Long userId) {
        return deleteById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    default int deleteUserByIds(Long[] userIds) {
        return deleteByIds(List.of(userIds));
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    SysUser checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phoneNumber 手机号码
     * @return 结果
     */
    SysUser checkPhoneUnique(String phoneNumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    SysUser checkEmailUnique(String email);
}
