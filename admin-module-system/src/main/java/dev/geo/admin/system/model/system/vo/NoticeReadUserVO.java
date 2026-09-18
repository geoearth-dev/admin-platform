package dev.geo.admin.system.model.system.vo;

import lombok.Data;
import java.time.Instant;

/** 公告已读用户信息。 */
@Data
public class NoticeReadUserVO {
    private Long userId;
    private String userName;
    private String nickName;
    private String deptName;
    private String phoneNumber;
    private Instant readTime;
}
