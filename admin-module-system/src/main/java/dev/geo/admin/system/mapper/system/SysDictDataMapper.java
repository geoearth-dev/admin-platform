package dev.geo.admin.system.mapper.system;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.DictDataPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysDictData;

import java.util.List;

/**
 * 字典表 数据层
 */
public interface SysDictDataMapper extends BaseMapperX<SysDictData> {
    default PageResult<SysDictData> selectPage(DictDataPageReqDTO query) {
        return selectPage(query, new LambdaQueryWrapper<SysDictData>()
                .eq(StrUtil.isNotBlank(query.getDictType()), SysDictData::getDictType, query.getDictType())
                .like(StrUtil.isNotBlank(query.getDictLabel()), SysDictData::getDictLabel, query.getDictLabel())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysDictData::getStatus, query.getStatus())
                .orderByAsc(SysDictData::getDictSort)
                .orderByDesc(SysDictData::getCreateTime)
                .orderByDesc(SysDictData::getId));
    }
    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    default List<SysDictData> selectDictDataList(SysDictData query) {
        return selectList(new LambdaQueryWrapper<SysDictData>()
                .eq(StrUtil.isNotBlank(query.getDictType()), SysDictData::getDictType, query.getDictType())
                .like(StrUtil.isNotBlank(query.getDictLabel()), SysDictData::getDictLabel, query.getDictLabel())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysDictData::getStatus, query.getStatus())
                .orderByAsc(SysDictData::getDictSort)
                .orderByDesc(SysDictData::getId));
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    default List<SysDictData> selectDictDataByType(String dictType) {
        return selectList(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, UserConstants.DICT_NORMAL)
                .orderByAsc(SysDictData::getDictSort)
                .orderByDesc(SysDictData::getId));
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    default String selectDictLabel(String dictType, String dictValue) {
        SysDictData data = selectOne(new LambdaQueryWrapper<SysDictData>()
                .select(SysDictData::getDictLabel)
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue)
                .last("limit 1"));
        return data == null ? null : data.getDictLabel();
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    default SysDictData selectDictDataById(Long id) {
        return selectById(id);
    }

    /**
     * 查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    default int countDictDataByType(String dictType) {
        return Math.toIntExact(selectCount(SysDictData::getDictType, dictType));
    }

    /**
     * 通过字典ID删除字典数据信息
     *
     * @param dictCode 字典数据ID
     * @return 结果
     */
    default int deleteDictDataById(Long id) {
        return deleteById(id);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果
     */
    default int deleteDictDataByIds(Long[] ids) {
        return deleteByIds(List.of(ids));
    }

    /**
     * 新增字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    default int insertDictData(SysDictData dictData) {
        return insert(dictData);
    }

    /**
     * 修改字典数据信息
     *
     * @param dictData 字典数据信息
     * @return 结果
     */
    default int updateDictData(SysDictData dictData) {
        return updateById(dictData);
    }

    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    default int updateDictDataType(String oldDictType, String newDictType) {
        return update(null, new LambdaUpdateWrapper<SysDictData>()
                .set(SysDictData::getDictType, newDictType)
                .eq(SysDictData::getDictType, oldDictType));
    }
}
