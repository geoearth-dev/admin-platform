package dev.geo.admin.system.model.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.geo.admin.excel.annotation.Excel;
import dev.geo.admin.mybatis.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统参数配置实体，对应 {@code sys_config} 表。
 */
@Setter
@Getter
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    /**
     * 主键。
     */
    @Excel(name = "参数主键", cellType = Excel.ColumnType.NUMERIC)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 参数名称
     */
    @Excel(name = "参数名称")
    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    private String configName;

    /**
     * 参数键名
     */
    @Excel(name = "参数键名")
    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名不能超过100个字符")
    private String configKey;

    /**
     * 参数键值
     */
    @Excel(name = "参数键值")
    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值不能超过500个字符")
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否")
    @NotBlank(message = "系统内置标识不能为空")
    @Pattern(regexp = "^[YN]$", message = "系统内置标识只能为Y或N")
    private String configType;
}
