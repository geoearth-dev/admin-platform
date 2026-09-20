package dev.geo.admin.generator.service;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geo.admin.common.constant.Constants;
import dev.geo.admin.common.constant.GenConstants;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.generator.mapper.GenTableColumnMapper;
import dev.geo.admin.generator.mapper.GenTableMapper;
import dev.geo.admin.generator.model.GenTable;
import dev.geo.admin.generator.model.GenTableColumn;
import dev.geo.admin.generator.model.dto.GenTablePageReqDTO;
import dev.geo.admin.generator.util.GenUtils;
import dev.geo.admin.generator.util.VelocityInitializer;
import dev.geo.admin.generator.util.VelocityUtils;
import dev.geo.admin.mybatis.model.converter.PageResultConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 业务 服务层实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class GenTableServiceImpl implements IGenTableService {
    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    private final GenTableMapper genTableMapper;

    private final GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务信息
     *
     * @param id 业务ID
     * @return 业务信息
     */
    @Override
    public GenTable selectGenTableById(Long id) {
        GenTable genTable = genTableMapper.selectGenTableById(id);
        if (genTable == null) throw new ServiceException("生成配置不存在");
        setTableFromOptions(genTable);
        return genTable;
    }

    /**
     * 查询业务列表
     *
     * @param query 业务信息
     * @return 业务集合
     */
    @Override
    public PageResult<GenTable> selectGenTablePage(GenTablePageReqDTO query) {
        IPage<GenTable> page = new Page<>(query.getPageNum(), query.getPageSize());
        return PageResultConverter.of(genTableMapper.selectGenTablePage(page, query));
    }

    /**
     * 查询据库列表
     *
     * @param query 业务信息
     * @return 数据库表集合
     */
    @Override
    public PageResult<GenTable> selectDbTablePage(GenTablePageReqDTO query) {
        IPage<GenTable> page = new Page<>(query.getPageNum(), query.getPageSize());
        return PageResultConverter.of(genTableMapper.selectDbTablePage(page, query));
    }

    /**
     * 查询据库列表
     *
     * @param tableNames 表名称组
     * @return 数据库表集合
     */
    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        if (tableNames == null || tableNames.length == 0) return List.of();
        return genTableMapper.selectDbTableListByNames(tableNames);
    }

    /**
     * 查询所有表信息
     *
     * @return 表信息集合
     */
    @Override
    public List<GenTable> selectGenTableAll() {
        return genTableMapper.selectGenTableAll();
    }

    /**
     * 修改业务
     *
     * @param genTable 业务信息
     */
    @Override
    @Transactional
    public void updateGenTable(GenTable genTable) {
        if (genTable.getId() == null || genTableMapper.selectGenTableById(genTable.getId()) == null) {
            throw new ServiceException("生成配置不存在");
        }
        Set<Long> columnIds = genTableColumnMapper.selectGenTableColumnListByTableId(genTable.getId()).stream().map(GenTableColumn::getId).collect(Collectors.toSet());
        if (genTable.getColumns() == null || genTable.getColumns().stream().anyMatch(column -> !columnIds.contains(column.getId()))) {
            throw new ServiceException("字段不属于当前生成配置");
        }
        String options = JSON.toJSONString(genTable.getParams());
        genTable.setOptions(options);
        int row = genTableMapper.updateGenTable(genTable);
        if (row > 0) {
            for (GenTableColumn genTableColumn : genTable.getColumns()) {
                genTableColumnMapper.updateGenTableColumn(genTableColumn);
            }
        }
    }

    /**
     * 删除业务对象
     *
     * @param tableIds 需要删除的数据ID
     */
    @Override
    @Transactional
    public void deleteGenTableByIds(Long[] tableIds) {
        genTableColumnMapper.deleteGenTableColumnByIds(tableIds);
        genTableMapper.deleteGenTableByIds(tableIds);
    }

    /**
     * 创建表
     *
     * @param sql 创建表语句
     * @return 结果
     */
    @Override
    public boolean createTable(String sql) {
        return genTableMapper.createTable(sql) == 0;
    }

    /**
     * 导入表结构
     *
     * @param tableList 导入表列表
     */
    @Override
    @Transactional
    public void importGenTable(List<GenTable> tableList, String operName) {
        try {
            if (tableList.isEmpty()) throw new ServiceException("没有可导入的数据表");
            for (GenTable table : tableList) {
                if (genTableMapper.selectGenTableByName(table.getTableName()) != null) {
                    throw new ServiceException("表已导入：" + table.getTableName());
                }
                String tableName = table.getTableName();
                GenUtils.initTable(table, operName);
                int row = genTableMapper.insertGenTable(table);
                if (row > 0) {
                    // 保存列信息
                    List<GenTableColumn> genTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
                    for (GenTableColumn column : genTableColumns) {
                        GenUtils.initColumnField(column, table);
                        genTableColumnMapper.insertGenTableColumn(column);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("导入失败：" + e.getMessage());
        }
    }

    /**
     * 预览代码
     *
     * @param tableId 表编号
     * @return 预览数据列表
     */
    @Override
    public Map<String, String> previewCode(Long tableId) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableById(tableId);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table);
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            dataMap.put(VelocityUtils.getFileName(template, table), sw.toString());
        }
        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     *
     * @param tableName 表名称
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String tableName) {
        return downloadCode(new String[]{tableName});
    }

    /**
     * 生成代码（自定义路径）
     *
     * @param tableName 表名称
     */
    @Override
    public void generatorCode(String tableName) {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        // 设置主子表信息
        setSubTable(table);
        // 设置主键列信息
        setPkColumn(table);

        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table);
        for (String template : templates) {
            StringWriter writer = new StringWriter();
            Velocity.getTemplate(template, Constants.UTF8).merge(context, writer);
            try {
                FileUtils.writeStringToFile(new File(getGenPath(table, template)), writer.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new ServiceException("写入生成文件失败：" + template);
            }
        }
    }

    /**
     * 同步数据库
     *
     * @param tableName 表名称
     */
    @Override
    @Transactional
    public void synchDb(String tableName) {
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        if (table == null) throw new ServiceException("生成配置不存在");
        List<GenTableColumn> tableColumns = table.getColumns();
        Map<String, GenTableColumn> tableColumnMap = tableColumns.stream().collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity()));

        List<GenTableColumn> dbTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
        if (ObjUtil.isEmpty(dbTableColumns)) {
            throw new ServiceException("同步数据失败，原表结构不存在");
        }
        List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).toList();

        dbTableColumns.forEach(column -> {
            GenUtils.initColumnField(column, table);
            if (tableColumnMap.containsKey(column.getColumnName())) {
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
                column.setId(prevColumn.getId());
                // 同步物理列类型、主键信息；保留用户编辑过的生成配置。
                column.setJavaField(prevColumn.getJavaField());
                if (Objects.equals(column.getColumnType(), prevColumn.getColumnType())) column.setJavaType(prevColumn.getJavaType());
                column.setIsInsert(prevColumn.getIsInsert());
                column.setIsEdit(prevColumn.getIsEdit());
                column.setIsList(prevColumn.getIsList());
                column.setIsQuery(prevColumn.getIsQuery());
                column.setIsRequired(prevColumn.getIsRequired());
                column.setDictType(prevColumn.getDictType());
                column.setQueryType(prevColumn.getQueryType());
                column.setHtmlType(prevColumn.getHtmlType());
                column.setSort(prevColumn.getSort());
                genTableColumnMapper.updateGenTableColumn(column);
            } else {
                genTableColumnMapper.insertGenTableColumn(column);
            }
        });

        List<GenTableColumn> delColumns = tableColumns.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName())).collect(Collectors.toList());
        if (ObjUtil.isNotEmpty(delColumns)) {
            genTableColumnMapper.deleteGenTableColumns(delColumns);
        }
    }

    /**
     * 批量生成代码（下载方式）
     *
     * @param tableNames 表数组
     * @return 数据
     */
    @Override
    public byte[] downloadCode(String[] tableNames) {
        if (tableNames == null || tableNames.length == 0) throw new ServiceException("请选择要生成的表");
        Map<String, String> files = new LinkedHashMap<>();
        for (String tableName : new LinkedHashSet<>(Arrays.asList(tableNames))) {
            GenTable table = genTableMapper.selectGenTableByName(tableName);
            setSubTable(table);
            setPkColumn(table);
            VelocityInitializer.initVelocity();
            VelocityContext context = VelocityUtils.prepareContext(table);
            for (String template : VelocityUtils.getTemplateList(table)) {
                StringWriter writer = new StringWriter();
                Velocity.getTemplate(template, Constants.UTF8).merge(context, writer);
                String fileName = VelocityUtils.getFileName(template, table);
                String previous = files.putIfAbsent(fileName, writer.toString());
                if (previous != null && !previous.equals(writer.toString())) {
                    throw new ServiceException("生成文件名冲突，请调整类名或分批生成：" + fileName);
                }
            }
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output)) {
            for (Map.Entry<String, String> file : files.entrySet()) writeToZip(zip, file.getKey(), file.getValue());
        } catch (IOException e) {
            throw new ServiceException("生成ZIP失败");
        }
        return output.toByteArray();
    }

    /**
     * 将字符串内容写入ZIP输出流
     *
     * @param zip      ZIP输出流
     * @param fileName ZIP条目名称（即文件名）
     * @param content  要写入的内容
     */
    private void writeToZip(ZipOutputStream zip, String fileName, String content) {
        try {
            zip.putNextEntry(new ZipEntry(fileName));
            IOUtils.write(content, zip, Constants.UTF8);
            zip.flush();
            zip.closeEntry();
        } catch (IOException e) {
            throw new ServiceException("写入ZIP文件失败：" + fileName);
        }
    }

    /**
     * 修改保存参数校验
     *
     * @param genTable 业务信息
     */
    @Override
    public void validateEdit(GenTable genTable) {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory())) {
            String options = JSON.toJSONString(genTable.getParams());
            JSONObject paramsObj = JSON.parseObject(options);
            if (StrUtil.isEmpty(paramsObj.getString(GenConstants.TREE_CODE))) {
                throw new ServiceException("树编码字段不能为空");
            } else if (StrUtil.isEmpty(paramsObj.getString(GenConstants.TREE_PARENT_CODE))) {
                throw new ServiceException("树父编码字段不能为空");
            } else if (StrUtil.isEmpty(paramsObj.getString(GenConstants.TREE_NAME))) {
                throw new ServiceException("树名称字段不能为空");
            }
        } else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory())) {
            if (StrUtil.isEmpty(genTable.getSubTableName())) {
                throw new ServiceException("关联子表的表名不能为空");
            } else if (StrUtil.isEmpty(genTable.getSubTableFkName())) {
                throw new ServiceException("子表关联的外键名不能为空");
            }
        }
    }

    /**
     * 设置主键列信息
     *
     * @param table 业务表信息
     */
    public void setPkColumn(GenTable table) {
        if (table == null || table.getColumns() == null) throw new ServiceException("生成配置不存在");
        List<GenTableColumn> primaryKeys = table.getColumns().stream().filter(GenTableColumn::isPk).toList();
        if (primaryKeys.size() != 1) throw new ServiceException("BaseMapperX 生成要求表具有一个主键：" + table.getTableName());
        table.setPkColumn(primaryKeys.get(0));
        if (table.isSub()) setPkColumn(table.getSubTable());
    }

    /**
     * 设置主子表信息
     *
     * @param table 业务表信息
     */
    public void setSubTable(GenTable table) {
        if (table == null) throw new ServiceException("生成配置不存在");
        String subTableName = table.getSubTableName();
        if (table.isSub() && StrUtil.isNotEmpty(subTableName)) {
            table.setSubTable(genTableMapper.selectGenTableByName(subTableName));
            if (table.getSubTable() == null) throw new ServiceException("请先导入关联子表");
        }
    }

    /**
     * 设置代码生成其他选项值
     *
     * @param genTable 设置后的生成对象
     */
    public void setTableFromOptions(GenTable genTable) {
        JSONObject paramsObj = JSON.parseObject(genTable.getOptions());
        if (ObjUtil.isNotNull(paramsObj)) {
            String treeCode = paramsObj.getString(GenConstants.TREE_CODE);
            String treeParentCode = paramsObj.getString(GenConstants.TREE_PARENT_CODE);
            String treeName = paramsObj.getString(GenConstants.TREE_NAME);
            Long parentMenuId = paramsObj.getLongValue(GenConstants.PARENT_MENU_ID);
            String parentMenuName = paramsObj.getString(GenConstants.PARENT_MENU_NAME);
            boolean isView = paramsObj.getBooleanValue(GenConstants.GEN_VIEW);

            genTable.setTreeCode(treeCode);
            genTable.setTreeParentCode(treeParentCode);
            genTable.setTreeName(treeName);
            genTable.setParentMenuId(parentMenuId);
            genTable.setParentMenuName(parentMenuName);
            genTable.setView(isView);
        }
    }

    /**
     * 获取代码生成地址
     *
     * @param table    业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template) {
        String genPath = table.getGenPath();
        java.nio.file.Path root = java.nio.file.Path.of(StrUtil.isBlank(genPath) || "/".equals(genPath)
                ? System.getProperty("user.dir") : genPath);
        if (!root.isAbsolute()) throw new ServiceException("自定义生成路径必须是绝对路径");
        root = root.normalize();
        java.nio.file.Path target = root.resolve(VelocityUtils.getFileName(template, table)).normalize();
        if (!target.startsWith(root)) throw new ServiceException("非法生成路径");
        return target.toString();
    }
}