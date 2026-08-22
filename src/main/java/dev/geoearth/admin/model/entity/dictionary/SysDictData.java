package dev.geoearth.admin.model.entity.dictionary;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据实体。
 */
@Data
@TableName(value = "sys_dict_data", autoResultMap = true)
@Schema(description = "字典数据")
public class SysDictData {

    @TableId(value = "dict_code", type = IdType.ASSIGN_ID)
    @Schema(description = "dict code")
    private Long dictCode;

    @Schema(description = "dict sort")
    private Integer dictSort;

    @Schema(description = "dict label")
    private String dictLabel;

    @Schema(description = "dict value")
    private String dictValue;

    @Schema(description = "dict type")
    private String dictType;

    @Schema(description = "css class")
    private String cssClass;

    @Schema(description = "list class")
    private String listClass;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "is default")
    private String isDefault;

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
}
