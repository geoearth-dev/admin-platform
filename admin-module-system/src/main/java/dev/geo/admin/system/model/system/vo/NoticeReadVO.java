package dev.geo.admin.system.model.system.vo;

import dev.geo.admin.system.model.system.entity.SysNotice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "首页公告与未读数量")
public class NoticeReadVO {
    /** 最新正常公告，最多5条。 */
    @Schema(description = "通知公告列表")
    private List<SysNotice> sysNotice;
    /** 当前用户全部正常公告的未读数量。 */
    @Schema(description = "未读数量")
    private int unreadCount;
}
