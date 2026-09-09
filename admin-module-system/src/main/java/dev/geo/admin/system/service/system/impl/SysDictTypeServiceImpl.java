package dev.geo.admin.system.service.system.impl;

import dev.geo.admin.common.constant.UserConstants;
import dev.geo.admin.common.core.model.PageResult;
import dev.geo.admin.common.exception.ServiceException;
import dev.geo.admin.system.mapper.system.SysDictDataMapper;
import dev.geo.admin.system.mapper.system.SysDictTypeMapper;
import dev.geo.admin.system.model.system.dto.DictTypePageReqDTO;
import dev.geo.admin.system.model.system.entity.SysDictData;
import dev.geo.admin.system.model.system.entity.SysDictType;
import dev.geo.admin.system.service.system.DictCacheService;
import dev.geo.admin.system.service.system.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 字典类型服务实现。
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements ISysDictTypeService {
    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    private final DictCacheService dictCache;

    @Override
    public PageResult<SysDictType> selectDictTypePage(DictTypePageReqDTO query) {
        return dictTypeMapper.selectPage(query);
    }

    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> cachedData = dictCache.getDictCache(dictType);
        if (cachedData != null && !cachedData.isEmpty()) {
            return cachedData;
        }
        List<SysDictData> databaseData = dictDataMapper.selectDictDataByType(dictType);
        if (databaseData == null || databaseData.isEmpty()) {
            return List.of();
        }
        dictCache.setDictCache(dictType, databaseData);
        return databaseData;
    }

    @Override
    public SysDictType selectDictTypeById(Long id) {
        return dictTypeMapper.selectDictTypeById(id);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    @Override
    public void deleteDictTypeByIds(Long[] ids) {
        for (Long id : ids) {
            SysDictType dictType = selectDictTypeById(id);
            if (dictType == null) {
                continue;
            }
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(dictType.getDictName() + "已分配字典数据，不能删除");
            }
            dictTypeMapper.deleteDictTypeById(id);
            dictCache.removeDictCache(dictType.getDictType());
        }
    }

    @Override
    public void loadingDictCache() {
        SysDictData query = new SysDictData();
        query.setStatus(UserConstants.DICT_NORMAL);
        Map<String, List<SysDictData>> groups = dictDataMapper.selectDictDataList(query).stream()
                .collect(Collectors.groupingBy(SysDictData::getDictType));
        groups.forEach((type, data) -> dictCache.setDictCache(type, data.stream()
                .sorted(Comparator.comparing(SysDictData::getDictSort))
                .toList()));
    }

    @Override
    public void clearDictCache() {
        dictCache.clearDictCache();
    }

    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    @Override
    public int insertDictType(SysDictType dict) {
        int rows = dictTypeMapper.insertDictType(dict);
        if (rows > 0) {
            dictCache.removeDictCache(dict.getDictType());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dict) {
        SysDictType previous = dictTypeMapper.selectDictTypeById(dict.getId());
        if (previous == null) {
            throw new ServiceException("字典类型不存在");
        }
        dictDataMapper.updateDictDataType(previous.getDictType(), dict.getDictType());
        int rows = dictTypeMapper.updateDictType(dict);
        if (rows > 0) {
            dictCache.removeDictCache(previous.getDictType());
            dictCache.setDictCache(dict.getDictType(), dictDataMapper.selectDictDataByType(dict.getDictType()));
        }
        return rows;
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dict) {
        SysDictType existing = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        return existing == null || Objects.equals(existing.getId(), dict.getId());
    }
}
