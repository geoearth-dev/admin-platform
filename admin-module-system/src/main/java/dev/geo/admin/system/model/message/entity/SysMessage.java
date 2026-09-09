package dev.geo.admin.system.model.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.mybatis.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


/**
 * 站内消息实体。
 */
@Getter
@Setter
@TableName("sys_message")
public class SysMessage extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送人用户ID，系统发送时可以为空。
     */
    private Long senderId;

    /**
     * 接收人用户ID。
     */
    private Long receiverId;

    private String title;
    private String content;
    private Integer category;
    private Integer messageLevel;
    private Integer module;
    private Integer businessType;
    private Long businessId;
    private String businessUrl;
    private Integer readStatus;
    private Instant readTime;

    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;
}
