package dev.geo.admin.system.model.message.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息模板分页查询参数。
 */
@Getter
@Setter
@Schema(description = "消息模板查询条件")
public class MessageTemplatePageReqDTO extends PageParam {
    @Schema(description = "模板编码")
    private String templateCode;
    @Schema(description = "模板名称")
    private String templateName;
    @Schema(description = "消息分类")
    private Integer category;
    @Schema(description = "状态：1启用，0停用")
    private String status;
}
