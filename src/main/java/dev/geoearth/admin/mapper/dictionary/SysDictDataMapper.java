package dev.geoearth.admin.mapper.dictionary;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.geoearth.admin.model.dto.dictionary.DictDataPageQuery;
import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典数据数据访问接口。
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {
    IPage<SysDictData> selectDictDataPage(Page<SysDictData> page, DictDataPageQuery query);
}
