package dev.geo.admin.system.model.message.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 批量标记消息已读的筛选条件。
 */
@Getter
@Setter
public class MessageReadAllDTO {
    private Integer category;
    private Integer module;
}
