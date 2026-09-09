package dev.geo.admin.system.model.message.dto;

import dev.geo.admin.mybatis.model.page.PageParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 当前用户消息分页查询参数。
 */
@Getter
@Setter
public class MessagePageReqDTO extends PageParam {
    private String title;
    private Integer category;
    private Integer messageLevel;
    private Integer module;
    private Integer readStatus;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date beginTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date endTime;
}
