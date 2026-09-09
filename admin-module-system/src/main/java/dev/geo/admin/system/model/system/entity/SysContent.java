package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import dev.geo.admin.mybatis.model.BaseEntity;
import lombok.*;

/**
 * 系统配置对象 system_content
 */
@Data
@TableName(value = "system_content")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysContent extends BaseEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 系统名称
     */
    private String sysName;

    /**
     * logo
     */
    private String loginLogo;

    private String logo;

    /**
     * 轮播图
     */
    private String carouselImage;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 版权方
     */
    private String copyright;

    /**
     * 备案号
     */
    private String recordNumber;

    /**
     * 删除标记
     */
    @TableLogic
    private Boolean delFlag;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;


}
