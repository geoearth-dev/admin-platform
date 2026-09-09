package dev.geo.admin.system.model.message.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息模板分页查询参数。
 */
@Getter
@Setter
public class MessageTemplatePageReqDTO extends PageParam {
    private String templateCode;
    private String templateName;
    private Integer category;
    private String status;
}
