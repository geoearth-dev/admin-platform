package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.geo.admin.common.constant.DateTimeFormat;
import dev.geo.admin.common.xss.Xss;
import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.excel.annotation.Excel.ColumnType;
import dev.geo.admin.excel.annotation.Excel.Type;
import dev.geo.admin.excel.annotation.Excels;
import dev.geo.admin.mybatis.model.BaseEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;
import java.util.List;

/**
 * 用户对象 sys_user
 */
@Setter
@Getter
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /**
     * 用户ID
     */
    @Excel(name = "用户序号", type = Type.EXPORT, cellType = ColumnType.NUMERIC, prompt = "用户编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String authId;

    /**
     * 部门ID
     */
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /**
     * 用户账号
     */
    @Excel(name = "登录名称")
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Excel(name = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码", cellType = ColumnType.TEXT)
    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    private String phoneNumber;

    /**
     * 用户性别
     */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 账号状态（0停用 1正常）
     */
    @Excel(name = "账号状态", readConverterExp = "0=停用,1=正常")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    /**
     * 最后登录IP
     */
    @Excel(name = "最后登录IP", type = Type.EXPORT)
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    @Excel(name = "最后登录时间", width = 30, dateFormat = DateTimeFormat.DATE_TIME_PATTERN, type = Type.EXPORT)
    private Instant lastLoginTime;

    /**
     * 密码最后更新时间
     */
    private Instant passwordUpdateTime;

    /**
     * 部门对象
     */
    @Excels({
            @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
            @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    @TableField(exist = false)
    private SysDept dept;

    /**
     * 角色对象
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    /**
     * 角色组
     */
    @TableField(exist = false)
    private Long[] roleIds;

    /**
     * 岗位组
     */
    @TableField(exist = false)
    private Long[] postIds;

    /**
     * 角色ID
     */
    @TableField(exist = false)
    private Long roleId;

    public SysUser() {
    }

    public SysUser(Long id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return Long.valueOf(1L).equals(this.id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deptId", getDeptId())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("email", getEmail())
                .append("phoneNumber", getPhoneNumber())
                .append("sex", getSex())
                .append("avatar", getAvatar())
                .append("password", getPassword())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("lastLoginIp", getLastLoginIp())
                .append("lastLoginTime", getLastLoginTime())
                .append("passwordUpdateTime", getPasswordUpdateTime())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("dept", getDept())
                .toString();
    }
}
