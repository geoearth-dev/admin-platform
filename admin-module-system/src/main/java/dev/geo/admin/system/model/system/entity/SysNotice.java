package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.geo.admin.mybatis.model.BaseEntity;
import dev.geo.admin.common.xss.Xss;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 通知公告表 sys_notice
 */
@Getter
@Setter
@TableName("sys_notice")
@Schema(description = "通知公告")
public class SysNotice extends BaseEntity {
    /**
     * 公告ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "通知公告 ID")
    private Long id;

    /**
     * 公告标题
     */
    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    @Schema(description = "公告标题")
    private String noticeTitle;

    /**
     * 公告类型（1通知 2公告）
     */
    @Schema(description = "类型：1通知，2公告")
    private String noticeType;

    /**
     * 公告内容
     */
    @Schema(description = "公告内容")
    private String noticeContent;
    /**
     * 公告跳转地址
     */
    @Schema(description = "外链地址")
    private String link;
    /**
     * 公告头像地址
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "公告图片地址")
    private String avatar;
    /**
     * 公告状态（0关闭 1正常）
     */
    @Schema(description = "状态：1启用，0停用")
    private String status;

    /** 删除标志：0未删除，1已删除。 */
    @TableLogic
    @Schema(description = "删除标志：0未删除，1已删除", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer delFlag;

    /**
     * 是否已读
     */
    @JsonProperty("isRead")
    @TableField(exist = false)
    @Schema(description = "当前用户是否已读")
    private boolean isRead;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("noticeTitle", getNoticeTitle())
                .append("noticeType", getNoticeType())
                .append("noticeContent", getNoticeContent())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
