package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.mybatis.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

/**
 * 角色表 sys_role
 */
@Setter
@Getter
@TableName("sys_role")
@Schema(description = "角色")
public class SysRole extends BaseEntity {

    /**
     * 角色ID
     */
    @Excel(name = "角色序号", cellType = Excel.ColumnType.NUMERIC)
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "角色 ID")
    private Long id;

    /**
     * 角色名称
     */
    @Excel(name = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色权限
     */
    @Excel(name = "角色权限")
    @NotBlank(message = "权限字符不能为空")
    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
    @Schema(description = "角色标识，如 admin")
    private String roleKey;

    /**
     * 角色排序
     */
    @Excel(name = "角色排序")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）
     */
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限")
    @Schema(description = "数据范围：1全部，2指定部门，3本部门，4本部门及下级，5本人")
    private String dataScope;

    /**
     * 菜单树勾选是否父子联动（true：联动，false：独立勾选）
     */
    @Schema(description = "菜单勾选是否父子联动")
    private boolean menuCheckLinked;

    /**
     * 部门树勾选是否父子联动（true：联动，false：独立勾选）
     */
    @Schema(description = "部门勾选是否父子联动")
    private boolean deptCheckLinked;

    /**
     * 角色状态（0停用 1正常）
     */
    @Excel(name = "角色状态", readConverterExp = "0=停用,1=正常")
    @Schema(description = "状态：1启用，0停用")
    private String status;

    /**
     * 删除标志（0未删除 1已删除）
     */
    @TableLogic
    @Schema(description = "删除标志：0存在，2已删除")
    private String delFlag;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    @TableField(exist = false)
    @Schema(description = "用户是否已分配该角色")
    private boolean flag = false;

    /**
     * 菜单组
     */
    @TableField(exist = false)
    @Schema(description = "菜单 ID 列表")
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    @TableField(exist = false)
    @Schema(description = "部门 ID 列表")
    private Long[] deptIds;

    /**
     * 角色菜单权限
     */
    @TableField(exist = false)
    @Schema(description = "权限标识集合")
    private Set<String> permissions;

    @Schema(description = "是否超级管理员角色")
    public boolean isAdmin() {
        return isAdmin(this.id);
    }

    public static boolean isAdmin(Long id) {
        return id != null && 1L == id;
    }

    public SysRole() {
    }

    public SysRole(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("roleName", getRoleName())
                .append("roleKey", getRoleKey())
                .append("roleSort", getRoleSort())
                .append("dataScope", getDataScope())
                .append("menuCheckLinked", isMenuCheckLinked())
                .append("deptCheckLinked", isDeptCheckLinked())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
