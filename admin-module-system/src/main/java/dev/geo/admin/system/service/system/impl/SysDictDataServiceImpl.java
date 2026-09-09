package dev.geo.admin.system.service.system.impl;

import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.system.mapper.system.SysDictDataMapper;
import dev.geo.admin.system.model.system.dto.DictDataPageReqDTO;
import dev.geo.admin.system.model.system.entity.SysDictData;
import dev.geo.admin.system.service.system.ISysDictDataService;
import dev.geo.admin.system.service.system.DictCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 业务层处理
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements ISysDictDataService {
    private final SysDictDataMapper dictDataMapper;
    private final DictCacheService dictCacheResolver;

    @Override
    public PageResult<SysDictData> selectDictDataPage(DictDataPageReqDTO query) {
        return dictDataMapper.selectPage(query);
    }

    /**
     * 根据条件分页查询字典数据
     *
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteDictDataById(dictCode);
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(data.getDictType());
            dictCacheResolver.setDictCache(data.getDictType(), dictDataList);
        }
    }

    /**
     * 新增保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data) {
        int row = dictDataMapper.insertDictData(data);
        if (row > 0) {
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(data.getDictType());
            dictCacheResolver.setDictCache(data.getDictType(), dictDataList);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     *
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data) {
        int row = dictDataMapper.updateDictData(data);
        if (row > 0) {
            List<SysDictData> dictDataList = dictDataMapper.selectDictDataByType(data.getDictType());
            dictCacheResolver.setDictCache(data.getDictType(), dictDataList);
        }
        return row;
    }
}
