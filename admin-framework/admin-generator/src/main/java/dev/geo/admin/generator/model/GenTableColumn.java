package dev.geo.admin.generator.model;

import cn.hutool.core.util.StrUtil;
import dev.geo.admin.mybatis.model.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.StringJoiner;

/**
 * 代码生成业务字段表 gen_table_column
 */
@Getter
@Setter
public class GenTableColumn extends BaseEntity {

    /**
     * 父类属性名单，用于避免重复生成实体属性。
     *
     * <p>沿用原名单，包含基础实体和树实体属性，不根据模板类型区分。</p>
     */
    private static final String[] SUPER_COLUMNS = {
            "createBy", "createTime", "updateBy", "updateTime", "remark",
            "parentName", "parentId", "orderNum", "ancestors"
    };

    /**
     * 生成页面时仍需使用的父类属性白名单。
     */
    private static final String[] USABLE_COLUMNS = {
            "parentId", "orderNum", "remark"
    };
    // ==================== 数据库列信息 ====================

    /**
     * 字段编号。
     */
    private Long columnId;

    /**
     * 归属表编号。
     */
    private Long tableId;

    /**
     * 数据库列名称。
     */
    private String columnName;

    /**
     * 数据库列描述。
     */
    private String columnComment;

    /**
     * 数据库列类型。
     */
    private String columnType;

    // ==================== Java 属性映射 ====================

    /**
     * Java 类型。
     */
    private String javaType;

    /**
     * Java 属性名。
     */
    @NotBlank(message = "Java属性不能为空")
    private String javaField;

    // ==================== 字段生成标记 ====================

    /**
     * 是否主键：1 表示是。
     */
    private String isPk;

    /**
     * 是否自增：1 表示是。
     */
    private String isIncrement;

    /**
     * 是否必填：1 表示是。
     */
    private String isRequired;

    /**
     * 是否为插入字段：1 表示是。
     */
    private String isInsert;

    /**
     * 是否为编辑字段：1 表示是。
     */
    private String isEdit;

    /**
     * 是否为列表字段：1 表示是。
     */
    private String isList;

    /**
     * 是否为查询字段：1 表示是。
     */
    private String isQuery;

    // ==================== 查询与页面配置 ====================

    /**
     * 查询方式。
     *
     * <p>EQ：等于；NE：不等于；GT：大于；LT：小于；LIKE：模糊；BETWEEN：范围。</p>
     */
    private String queryType;

    /**
     * 页面显示类型。
     *
     * <p>input：文本框；textarea：文本域；select：下拉框；checkbox：复选框；
     * radio：单选框；datetime：日期控件；image：图片上传；upload：文件上传；
     * editor：富文本控件。</p>
     */
    private String htmlType;

    /**
     * 字典类型。
     */
    private String dictType;

    /**
     * 排序值。
     */
    private Integer sort;

    // ==================== 属性名称转换 ====================

    /**
     * 获取首字母大写的 Java 属性名，例如 userName 转为 UserName。
     */
    public String getCapJavaField() {
        return StrUtil.upperFirst(javaField);
    }

    // ==================== 字段标记判断 ====================

    /**
     * 当前字段是否为主键。
     */
    public boolean isPk() {
        return isPk(isPk);
    }

    public boolean isPk(String isPk) {
        return isYes(isPk);
    }

    /**
     * 当前字段是否自增。
     */
    public boolean isIncrement() {
        return isIncrement(isIncrement);
    }

    public boolean isIncrement(String isIncrement) {
        return isYes(isIncrement);
    }

    /**
     * 当前字段是否必填。
     */
    public boolean isRequired() {
        return isRequired(isRequired);
    }

    public boolean isRequired(String isRequired) {
        return isYes(isRequired);
    }

    /**
     * 当前字段是否参与插入。
     */
    public boolean isInsert() {
        return isInsert(isInsert);
    }

    public boolean isInsert(String isInsert) {
        return isYes(isInsert);
    }

    /**
     * 当前字段是否参与编辑。
     */
    public boolean isEdit() {
        return isEdit(isEdit);
    }

    public boolean isEdit(String isEdit) {
        return isYes(isEdit);
    }

    /**
     * 当前字段是否在列表中显示。
     */
    public boolean isList() {
        return isList(isList);
    }

    public boolean isList(String isList) {
        return isYes(isList);
    }

    /**
     * 当前字段是否用于查询。
     */
    public boolean isQuery() {
        return isQuery(isQuery);
    }

    public boolean isQuery(String isQuery) {
        return isYes(isQuery);
    }

    /**
     * 统一判断肯定标记；null、空串及其他值均返回 false。
     */
    private static boolean isYes(String value) {
        return StrUtil.equals("1", value);
    }

    // ==================== 父类属性判断 ====================

    /**
     * 当前 Java 属性是否属于父类属性名单。
     */
    public boolean isSuperColumn() {
        return isSuperColumn(javaField);
    }

    /**
     * 判断 Java 属性是否属于父类属性名单，忽略大小写。
     *
     * @param javaField Java 属性名，而非数据库列名
     * @return 是否属于父类属性名单
     */
    public static boolean isSuperColumn(String javaField) {
        return StrUtil.equalsAnyIgnoreCase(javaField, SUPER_COLUMNS);
    }

    /**
     * 当前 Java 属性是否属于生成页面时仍需使用的父类属性白名单。
     */
    public boolean isUsableColumn() {
        return isUsableColumn(javaField);
    }

    /**
     * 判断 Java 属性是否属于页面可用的父类属性白名单，忽略大小写。
     *
     * <p>该方法不是 isSuperColumn() 的取反，也不表示任意字段是否可用。</p>
     *
     * @param javaField Java 属性名，而非数据库列名
     * @return 是否属于页面可用的父类属性白名单
     */
    public static boolean isUsableColumn(String javaField) {
        return StrUtil.equalsAnyIgnoreCase(javaField, SUPER_COLUMNS);
    }

    // ==================== 注释转换 ====================

    /**
     * 从列描述中提取转换表达式。
     *
     * <p>沿用原格式：中文括号内使用普通空格分隔，每项首字符为编码，
     * 剩余内容为描述。例如：状态（0正常 1停用）转为 0=正常,1=停用。</p>
     *
     * <p>不额外支持多位编码、英文括号或其他分隔格式。
     * 未提取到内容，或分隔后没有有效项时，返回原列描述。</p>
     *
     * @return 转换表达式；无内容时返回原列描述，原值为 null 时返回 null
     */
    public String readConverterExp() {
        String remarks = StrUtil.subBetween(columnComment, "（", "）");
        if (StrUtil.isEmpty(remarks)) {
            return columnComment;
        }

        StringJoiner converter = new StringJoiner(",").setEmptyValue(columnComment);

        // 按普通空格分隔，忽略空项，不额外修剪每项内容。
        for (String item : StrUtil.split(remarks, ' ', false, true)) {
            converter.add(item.charAt(0) + "=" + item.substring(1));
        }

        return converter.toString();
    }
}
