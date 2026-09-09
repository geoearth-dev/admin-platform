package dev.geo.admin.system.model.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;


/**
 * 用户与认证中心关系  对象 sys_user_auth_product
 */
@Data
@TableName(value = "sys_user_auth_product")
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUserAuthProduct {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 统一身份认证id
     */
    private String authId;

    /**
     * 认证平台类型
     */
    private Integer authProductType;


}
