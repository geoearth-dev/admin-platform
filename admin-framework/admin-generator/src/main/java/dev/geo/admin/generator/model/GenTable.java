package dev.geo.admin.generator.model;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.GenConstants;
import dev.geo.admin.mybatis.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 业务表 gen_table
 */
@Getter
@Setter
@Schema(description = "表生成配置")
public class GenTable extends BaseEntity {
    // ==================== 表基本信息 ====================
    /**
     * 编号
     */
    @Schema(description = "生成表配置 ID")
    private Long id;

    /**
     * 表名称
     */
    @NotBlank(message = "表名称不能为空")
    @Schema(description = "数据库表名")
    private String tableName;

    /**
     * 表描述
     */
    @NotBlank(message = "表描述不能为空")
    @Schema(description = "表说明")
    private String tableComment;
    // ==================== 主子表配置 ====================
    /**
     * 关联父表的表名
     */
    @Schema(description = "关联子表的表名")
    private String subTableName;

    /**
     * 本表关联父表的外键名
     */
    @Schema(description = "子表中关联主表的外键字段")
    private String subTableFkName;
    // ==================== 代码生成配置 ====================
    /**
     * 实体类名称(首字母大写)
     */
    @NotBlank(message = "实体类名称不能为空")
    @Schema(description = "实体类名，首字母大写")
    private String className;

    /**
     * 使用的模板（crud单表操作 tree树表操作 sub主子表操作）
     */
    @Schema(description = "模板类型：crud单表，tree树表，sub主子表")
    private String tplCategory;


    /**
     * 生成包路径
     */
    @NotBlank(message = "生成包路径不能为空")
    @Schema(description = "生成代码的基础包名")
    private String packageName;

    /**
     * 生成模块名
     */
    @NotBlank(message = "生成模块名不能为空")
    @Schema(description = "模块名称")
    private String moduleName;

    /**
     * 生成业务名
     */
    @NotBlank(message = "生成业务名不能为空")
    @Schema(description = "业务名称")
    private String businessName;

    /**
     * 生成功能名
     */
    @NotBlank(message = "生成功能名不能为空")
    @Schema(description = "功能名称")
    private String functionName;

    /**
     * 生成作者
     */
    @NotBlank(message = "作者不能为空")
    @Schema(description = "代码作者")
    private String functionAuthor;

    /**
     * 表单布局（单列 双列 三列）
     */
    @Schema(description = "表单列数：1、2或3")
    private Integer formColNum;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @Schema(description = "生成方式：0下载压缩包，1写入指定目录")
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    @Schema(description = "服务器上的生成目录")
    private String genPath;
    // ==================== 生成关联信息 ====================
    /**
     * 主键信息
     */
    @Schema(description = "主键字段配置")
    private GenTableColumn pkColumn;

    /**
     * 子表信息
     */
    @Schema(description = "子表配置")
    private GenTable subTable;

    /**
     * 表列信息
     */
    @Valid
    @Schema(description = "字段配置列表")
    private List<GenTableColumn> columns;
    // ==================== 扩展生成选项 ====================
    /**
     * 其它生成选项
     */
    @Schema(description = "扩展生成选项，JSON 字符串")
    private String options;

    /**
     * 树编码字段
     */
    @Schema(description = "树节点 ID 字段")
    private String treeCode;

    /**
     * 树父编码字段
     */
    @Schema(description = "树节点的父 ID 字段")
    private String treeParentCode;

    /**
     * 树名称字段
     */
    @Schema(description = "树节点显示名称字段")
    private String treeName;

    /**
     * 上级菜单ID字段
     */
    @Schema(description = "生成菜单的上级菜单 ID")
    private Long parentMenuId;

    /**
     * 上级菜单名称字段
     */
    @Schema(description = "上级菜单名称")
    private String parentMenuName;

    /**
     * 是否生成详情页
     */
    @Schema(description = "是否生成详情页")
    private boolean isView;
    // ==================== 模板类型判断 ====================

    /**
     * 是否使用单表模板。
     */
    public boolean isCrud() {
        return isCrud(tplCategory);
    }

    /**
     * 判断指定模板类型是否为单表模板。
     */
    public static boolean isCrud(String tplCategory) {
        return StrUtil.equals(GenConstants.TPL_CRUD, tplCategory);
    }

    /**
     * 是否使用树表模板。
     */
    public boolean isTree() {
        return isTree(tplCategory);
    }

    /**
     * 判断指定模板类型是否为树表模板。
     */
    public static boolean isTree(String tplCategory) {
        return StrUtil.equals(GenConstants.TPL_TREE, tplCategory);
    }

    /**
     * 是否使用主子表模板。
     */
    public boolean isSub() {
        return isSub(tplCategory);
    }

    /**
     * 判断指定模板类型是否为主子表模板。
     */
    public static boolean isSub(String tplCategory) {
        return StrUtil.equals(GenConstants.TPL_SUB, tplCategory);
    }

    // ==================== 父类字段判断 ====================

    /**
     * 判断 Java 属性是否由当前模板对应的父类提供。
     *
     * @param javaField Java 属性名，而非数据库列名
     * @return 是否属于父类字段
     */
    public boolean isSuperColumn(String javaField) {
        return isBaseEntity() && StrUtil.equalsAnyIgnoreCase(javaField,
                "creatorId", "createBy", "createTime", "updaterId", "updateBy", "updateTime", "remark");
    }

    /** Only inherit BaseEntity when the physical table actually contains all its persisted fields. */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isBaseEntity() {
        if (columns == null) return false;
        return java.util.stream.Stream.of("creator_id", "create_by", "create_time", "updater_id", "update_by", "update_time", "remark")
                .allMatch(name -> columns.stream().anyMatch(column -> name.equals(column.getColumnName())));
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public List<GenTableColumn> getSaveColumns() {
        return (columns == null ? java.util.stream.Stream.<GenTableColumn>empty() : columns.stream()).filter(column -> column.isPk() || (!column.isAuditColumn()
                && (column.isInsert() || column.isEdit() || column.getColumnName().equals(treeParentCode)))).toList();
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public List<GenTableColumn> getQueryColumns() {
        return (columns == null ? java.util.stream.Stream.<GenTableColumn>empty() : columns.stream()).filter(GenTableColumn::isQuery).toList();
    }
}
