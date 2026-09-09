package dev.geo.admin.system.model.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.mybatis.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 站内消息模板实体。
 */
@Getter
@Setter
@TableName("sys_message_template")
public class SysMessageTemplate extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 业务调用使用的稳定模板编码。 */
    private String templateCode;

    private String templateName;
    private String titleTemplate;
    private String contentTemplate;
    private Integer category;
    private Integer messageLevel;

    /** 模板状态：0启用，1停用。 */
    private String status;

    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}
