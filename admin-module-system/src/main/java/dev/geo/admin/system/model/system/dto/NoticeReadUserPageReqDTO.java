package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/** 公告已读用户分页条件，searchValue 继承自分页基类。 */
@Getter
@Setter
@Schema(description = "公告阅读记录查询条件")
public class NoticeReadUserPageReqDTO extends PageParam {
    @NotNull(message = "公告ID不能为空")
    @Positive(message = "公告ID必须大于0")
    @Schema(description = "公告 ID")
    private Long noticeId;
}
