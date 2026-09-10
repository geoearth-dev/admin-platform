package dev.geo.admin.mybatis.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BaseEntity {

    /**
     * 搜索值
     */
    @JsonIgnore
    @TableField(exist = false)
    @Schema(description = "搜索值", example = "")
    private String searchValue;

    @Schema(description = "创建者id", example = "")
    @TableField(fill = FieldFill.INSERT)
    private Long creatorId;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "")
    private Instant createTime;


    @Schema(description = "更新者id", example = "")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updaterId;
    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "")
    private Instant updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "")
    private String remark;

    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    @Schema(description = "其他请求参数", example = "")
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

    public Object getParamByKey(String key) {
        if (params == null) {
            params = new HashMap<>();
        }
        return params.get(key);
    }
}
