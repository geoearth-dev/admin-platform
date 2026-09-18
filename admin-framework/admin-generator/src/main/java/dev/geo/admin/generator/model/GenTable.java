package dev.geo.admin.generator.model;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.common.constant.GenConstants;
import dev.geo.admin.mybatis.model.BaseEntity;
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
public class GenTable extends BaseEntity {
    // ==================== 表基本信息 ====================
    /**
     * 编号
     */
    private Long tableId;

    /**
     * 表名称
     */
    @NotBlank(message = "表名称不能为空")
    private String tableName;

    /**
     * 表描述
     */
    @NotBlank(message = "表描述不能为空")
    private String tableComment;
    // ==================== 主子表配置 ====================
    /**
     * 关联父表的表名
     */
    private String subTableName;

    /**
     * 本表关联父表的外键名
     */
    private String subTableFkName;
    // ==================== 代码生成配置 ====================
    /**
     * 实体类名称(首字母大写)
     */
    @NotBlank(message = "实体类名称不能为空")
    private String className;

    /**
     * 使用的模板（crud单表操作 tree树表操作 sub主子表操作）
     */
    private String tplCategory;

    /**
     * 前端类型（element-ui模版 element-plus模版 element-plus-typescript模版）
     */
    private String tplWebType;

    /**
     * 生成包路径
     */
    @NotBlank(message = "生成包路径不能为空")
    private String packageName;

    /**
     * 生成模块名
     */
    @NotBlank(message = "生成模块名不能为空")
    private String moduleName;

    /**
     * 生成业务名
     */
    @NotBlank(message = "生成业务名不能为空")
    private String businessName;

    /**
     * 生成功能名
     */
    @NotBlank(message = "生成功能名不能为空")
    private String functionName;

    /**
     * 生成作者
     */
    @NotBlank(message = "作者不能为空")
    private String functionAuthor;

    /**
     * 表单布局（单列 双列 三列）
     */
    private Integer formColNum;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    private String genPath;
    // ==================== 生成关联信息 ====================
    /**
     * 主键信息
     */
    private GenTableColumn pkColumn;

    /**
     * 子表信息
     */
    private GenTable subTable;

    /**
     * 表列信息
     */
    @Valid
    private List<GenTableColumn> columns;
    // ==================== 扩展生成选项 ====================
    /**
     * 其它生成选项
     */
    private String options;

    /**
     * 树编码字段
     */
    private String treeCode;

    /**
     * 树父编码字段
     */
    private String treeParentCode;

    /**
     * 树名称字段
     */
    private String treeName;

    /**
     * 上级菜单ID字段
     */
    private Long parentMenuId;

    /**
     * 上级菜单名称字段
     */
    private String parentMenuName;

    /**
     * 是否生成详情页
     */
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
        return isSuperColumn(tplCategory, javaField);
    }

    /**
     * 判断 Java 属性是否由指定模板对应的父类提供。
     *
     * <p>所有模板检查基础实体字段，树表模板额外检查树实体字段。
     * 字段名比较忽略大小写，保持现有生成规则。</p>
     *
     * @param tplCategory 模板类型
     * @param javaField   Java 属性名，而非数据库列名
     * @return 是否属于父类字段
     */
    public static boolean isSuperColumn(String tplCategory, String javaField) {
        return StrUtil.equalsAnyIgnoreCase(javaField, GenConstants.BASE_ENTITY)
                || (isTree(tplCategory)
                && StrUtil.equalsAnyIgnoreCase(javaField, GenConstants.TREE_ENTITY));
    }
}