package dev.geo.admin.mybatis.model.page;

import dev.geo.admin.mybatis.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "分页参数")
public class PageParam extends BaseEntity {
    private static final Integer PAGE_NUM = 1;
    private static final Integer PAGE_SIZE = 100;
    /**
     * 每页显示项目数 - 不分页
     * 例如，导出界面时，您可以将 {@link #pageSize} 设置为 -1 以查询所有数据而不分页。
     */
    public static final Integer PAGE_SIZE_NONE = -1;

    @Schema(description = "页码，从1开始", example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = PAGE_NUM;

    @Schema(description = "每页数量", example = "20")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = PAGE_SIZE;
}
