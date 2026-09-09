package dev.geo.admin.system.mapper.system;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.mybatis.mapper.BaseMapperX;
import dev.geo.admin.system.model.system.dto.DictTypePageReqDTO;
import dev.geo.admin.system.model.system.entity.SysDictType;

import java.util.List;

/**
 * 字典表 数据层
 */
public interface SysDictTypeMapper extends BaseMapperX<SysDictType> {
    default PageResult<SysDictType> selectPage(DictTypePageReqDTO query) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<SysDictType>()
                .like(StrUtil.isNotBlank(query.getDictName()), SysDictType::getDictName, query.getDictName())
                .like(StrUtil.isNotBlank(query.getDictType()), SysDictType::getDictType, query.getDictType())
                .eq(StrUtil.isNotBlank(query.getStatus()), SysDictType::getStatus, query.getStatus())
                .ge(query.getBeginTime() != null, SysDictType::getCreateTime, query.getBeginTime())
                .le(query.getEndTime() != null, SysDictType::getCreateTime,
                        query.getEndTime() == null ? null : DateUtil.endOfDay(query.getEndTime()))
                .orderByDesc(SysDictType::getCreateTime)
                .orderByDesc(SysDictType::getId);
        return selectPage(query, wrapper);
    }
    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    default List<SysDictType> selectDictTypeAll() {
        return selectList(new LambdaQueryWrapper<SysDictType>()
                .orderByDesc(SysDictType::getCreateTime)
                .orderByDesc(SysDictType::getId));
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    default SysDictType selectDictTypeById(Long id) {
        return selectById(id);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    default SysDictType selectDictTypeByType(String dictType) {
        return selectOne(SysDictType::getDictType, dictType);
    }

    /**
     * 通过字典ID删除字典信息
     *
     * @param dictId 字典ID
     * @return 结果
     */
    default int deleteDictTypeById(Long id) {
        return deleteById(id);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    default int deleteDictTypeByIds(Long[] ids) {
        return deleteByIds(List.of(ids));
    }

    /**
     * 新增字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    default int insertDictType(SysDictType dictType) {
        return insert(dictType);
    }

    /**
     * 修改字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    default int updateDictType(SysDictType dictType) {
        return updateById(dictType);
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    default SysDictType checkDictTypeUnique(String dictType) {
        return selectOne(SysDictType::getDictType, dictType);
    }
}
