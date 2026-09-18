package dev.geo.admin.system.model.system.vo;

import dev.geo.admin.system.model.system.entity.SysNotice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class NoticeReadVO {
    /** 最新正常公告，最多5条。 */
    private List<SysNotice> sysNotice;
    /** 当前用户全部正常公告的未读数量。 */
    private int unreadCount;
}
