package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.mybatis.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 */
@Setter
@Getter
@TableName("sys_dept")
@Schema(description = "部门")
public class SysDept extends BaseEntity {

    /**
     * 部门ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "部门 ID")
    private Long id;

    /**
     * 父部门ID
     */
    @Schema(description = "上级 ID，0表示根节点")
    private Long parentId;

    /**
     * 祖级列表
     */
    @Schema(description = "祖级部门 ID，逗号分隔")
    private String ancestors;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序，越小越靠前")
    private Integer orderNum;

    /**
     * 负责人
     */
    @Schema(description = "负责人")
    private String leader;

    /**
     * 联系电话
     */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")
    @Schema(description = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    @Schema(description = "邮箱")
    private String email;

    /**
     * 部门状态:0停用,1正常
     */
    @Schema(description = "状态：1启用，0停用")
    private String status;

    /**
     * 删除标志（0未删除 1已删除）
     */
    @TableLogic
    @Schema(description = "删除标志：0存在，2已删除")
    private String delFlag;

    /**
     * 父部门名称
     */
    @TableField(exist = false)
    @Schema(description = "上级名称")
    private String parentName;

    /**
     * 子部门
     */
    @TableField(exist = false)
    @Schema(description = "子节点")
    private List<SysDept> children = new ArrayList<SysDept>();

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("parentId", getParentId())
                .append("ancestors", getAncestors())
                .append("deptName", getDeptName())
                .append("orderNum", getOrderNum())
                .append("leader", getLeader())
                .append("phone", getPhone())
                .append("email", getEmail())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
