package dev.geo.admin.system.model.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.Instant;

/** 公告已读用户信息。 */
@Data
@Schema(description = "公告阅读记录")
public class NoticeReadUserVO {
    @Schema(description = "用户 ID")
    private Long userId;
    @Schema(description = "用户账号")
    private String userName;
    @Schema(description = "用户昵称")
    private String nickName;
    @Schema(description = "部门名称")
    private String deptName;
    @Schema(description = "手机号码")
    private String phoneNumber;
    @Schema(description = "阅读时间")
    private Instant readTime;
}
