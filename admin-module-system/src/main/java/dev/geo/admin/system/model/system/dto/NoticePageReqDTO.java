package dev.geo.admin.system.model.system.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 通知公告分页查询参数。
 */
@Getter
@Setter
public class NoticePageReqDTO extends PageParam {
    private String noticeTitle;
    private String noticeType;
    private String status;
    private String createBy;
}
