package dev.geoearth.admin.model.entity.dictionary;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型实体。
 */
@Data
@TableName(value = "system_dict_type", autoResultMap = true)
@Schema(description = "字典类型")
public class SysDictType {

    @TableId(value = "dict_id", type = IdType.ASSIGN_ID)
    @Schema(description = "dict id")
    private Long dictId;

    @Schema(description = "dict name")
    private String dictName;

    @Schema(description = "dict type")
    private String dictType;

    @Schema(description = "status")
    private String status;

    @Schema(description = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "备注")
    private String remark;
}
