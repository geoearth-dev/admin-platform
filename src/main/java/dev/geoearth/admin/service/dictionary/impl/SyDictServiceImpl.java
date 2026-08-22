package dev.geoearth.admin.service.dictionary.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geoearth.admin.common.core.entity.PageParam;
import dev.geoearth.admin.common.core.exception.BusinessException;
import dev.geoearth.admin.common.core.response.PageResponse;
import dev.geoearth.admin.mapper.dictionary.SysDictDataMapper;
import dev.geoearth.admin.mapper.dictionary.SysDictTypeMapper;
import dev.geoearth.admin.model.converter.dictionray.SysDictConverter;
import dev.geoearth.admin.model.dto.dictionary.DictDataPageQuery;
import dev.geoearth.admin.model.dto.dictionary.DictTypePageQuery;
import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import dev.geoearth.admin.model.entity.dictionary.SysDictType;
import dev.geoearth.admin.model.vo.dictionary.DictOptionVO;
import dev.geoearth.admin.service.dictionary.SysDictCacheService;
import dev.geoearth.admin.service.dictionary.SysDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统字典维护。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SyDictServiceImpl implements SysDictService {

    private final SysDictTypeMapper typeMapper;
    private final SysDictDataMapper dataMapper;
    private final SysDictCacheService dictCacheService;

    public PageResponse<SysDictType> listTypes(PageParam pageParam, DictTypePageQuery query) {
        LambdaQueryWrapper<SysDictType> wrapper = Wrappers.lambdaQuery(SysDictType.class);
        // 按名称模糊查询
        wrapper.like(StringUtils.hasText(query.getDictName()), SysDictType::getDictName, query.getDictName());
        // 按状态查询
        wrapper.eq(StringUtils.hasText(query.getStatus()), SysDictType::getStatus, query.getStatus());
        // 创建时间倒序
        wrapper.orderByDesc(SysDictType::getCreateTime);
        Page<SysDictType> page = Page.of(pageParam.getPageNum(), pageParam.getPageSize());
        IPage<SysDictType> result = typeMapper.selectPage(page, wrapper);
        return PageResponse.of(result);
    }

    public PageResponse<SysDictData> listData(PageParam pageParam, DictDataPageQuery query) {
        Page<SysDictData> page = Page.of(pageParam.getPageNum(), pageParam.getPageSize());
        IPage<SysDictData> result = dataMapper.selectDictDataPage(page, query);
        return PageResponse.of(result);
    }

    @Transactional
    public int createType(SysDictType type) {
        if (checkDictType(type)) {
            throw new BusinessException("字典类型已存在");
        }
        int row = typeMapper.insert(type);
        if (row > 0) {
            dictCacheService.putItems(type.getDictType(), null);
        }
        return row;
    }

    @Transactional
    public int updateType(SysDictType dict) {
        if (checkDictType(dict)) {
            throw new BusinessException("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        SysDictType oldDict = requireType(dict.getDictId());
        String oldType = oldDict.getDictType();
        String newType = dict.getDictType();
        dataMapper.update(Wrappers.<SysDictData>lambdaUpdate()
                .eq(SysDictData::getDictType, oldType)
                .set(SysDictData::getDictType, newType)
        );
        int row = typeMapper.updateById(dict);
        if (row > 0) {
            dictCacheService.deleteItems(oldType);
            putDataListCacheByType(newType);
        }
        return row;
    }

    @Transactional
    public int createData(SysDictData data) {
        int row = dataMapper.insert(data);
        if (row > 0) {
            putDataListCacheByType(data.getDictType());
        }
        return row;
    }

    @Transactional
    public int updateData(SysDictData data) {
        int row = dataMapper.updateById(data);
        if (row > 0) {
            putDataListCacheByType(data.getDictType());
        }
        return row;
    }

    @Transactional
    public void disableType(Long dictId) {
        SysDictType type = requireType(dictId);
        type.setStatus("1");
        dataMapper.update(Wrappers.<SysDictData>lambdaUpdate()
                .eq(SysDictData::getDictType, type.getDictType())
                .set(SysDictData::getStatus, "1"));
        dictCacheService.deleteItems(type.getDictType());
    }

    @Transactional
    public void enableType(Long dictId) {
        SysDictType type = requireType(dictId);
        type.setStatus("0");
        typeMapper.updateById(type);
        dataMapper.update(Wrappers.<SysDictData>lambdaUpdate()
                .eq(SysDictData::getDictType, type.getDictType())
                .set(SysDictData::getStatus, "1"));

        putDataListCacheByType(type.getDictType());
    }

    @Transactional
    public void disableData(Long dictCode) {
        SysDictData data = requireData(dictCode);
        data.setStatus("1");
        dataMapper.updateById(data);
        putDataListCacheByType(data.getDictType());
    }

    @Transactional
    public void enableData(Long dictCode) {
        SysDictData data = requireData(dictCode);
        data.setStatus("0");
        dataMapper.updateById(data);
        putDataListCacheByType(data.getDictType());
    }

    private SysDictType requireType(Long id) {
        SysDictType type = typeMapper.selectById(id);
        if (type == null) {
            throw new BusinessException(404, "字典类型不存在");
        }
        return type;
    }

    private SysDictType requireType(String dictType) {
        SysDictType type = typeMapper.selectOne(Wrappers.<SysDictType>lambdaQuery()
                .eq(SysDictType::getDictType, dictType));
        if (type == null) {
            throw new BusinessException(404, "字典类型不存在");
        }
        return type;
    }

    private SysDictData requireData(Long code) {
        SysDictData data = dataMapper.selectById(code);
        if (data == null) {
            throw new BusinessException(404, "字典数据不存在");
        }
        return data;
    }

    private List<SysDictData> requireDataListByType(String dictType) {
        return dataMapper.selectList(
                Wrappers.<SysDictData>lambdaQuery()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, "0")
        );
    }

    private void putDataListCacheByType(String dictType) {
        List<SysDictData> dictDataList = requireDataListByType(dictType);
        dictCacheService.putItems(dictType, dictDataList);
    }

    /**
     * 校验字典类型称是否存在
     *
     * @param dict 字典类型
     * @return 结果
     */
    private boolean checkDictType(SysDictType dict) {
        return typeMapper.selectCount(Wrappers.<SysDictType>lambdaQuery().eq(SysDictType::getDictType, dict.getDictType())) > 0;
    }

    /**
     * 全量重新加载字典。
     */
    @Override
    public void reloadAllDictData() {
        List<SysDictData> allItems = dataMapper.selectList(Wrappers.<SysDictData>lambdaQuery()
                .eq(SysDictData::getStatus, "0")
        );
        Map<String, List<SysDictData>> grouped = allItems.stream()
                .collect(
                        Collectors.groupingBy(SysDictData::getDictType)
                );
        grouped.forEach(dictCacheService::putItems);
    }

    @Override
    public List<DictOptionVO> getItems(String dictType) {
        // 查询缓存
        try {
            List<DictOptionVO> cachedItems = dictCacheService.getItems(dictType);
            /*
             * null表示缓存不存在。
             * 空集合表示已经查询过，但确实没有数据。
             */
            if (cachedItems != null) {
                return cachedItems;
            }
        } catch (Exception exception) {
            log.warn("读取字典缓存失败，dictType={}", dictType, exception);
        }

        // 查询数据库
        List<SysDictData> dataList = requireDataListByType(dictType);

        // 转成缓存及返回对象
        List<DictOptionVO> optionList = dataList.stream().map(SysDictConverter::toVO).toList();

        // 写入缓存，空集合也需要缓存
        try {
            dictCacheService.putItems(dictType, dataList);
        } catch (Exception exception) {
            log.warn("写入字典缓存失败，dictType={}", dictType, exception);
        }

        return optionList;
    }

    @Override
    public Map<String, String> getLabelMap(String dictType) {
        return getItems(dictType)
                .stream()
                .filter(item ->
                        item.getValue() != null && !item.getValue().isBlank()
                )
                .collect(Collectors.toMap(
                        DictOptionVO::getValue,
                        DictOptionVO::getLabel,
                        /*
                         * 如果数据库出现重复value，
                         * 保留第一条，避免抛异常。
                         */
                        (first, second) -> first,
                        LinkedHashMap::new
                ));
    }

    @Override
    public String getLabel(String dictType, String dictValue) {
        if (dictValue == null || dictValue.isBlank()) {
            return null;
        }
        return getItems(dictType)
                .stream()
                .filter(item -> Objects.equals(item.getValue(), dictValue))
                .map(DictOptionVO::getLabel)
                .findFirst()
                .orElse(null);
    }

}
