package dev.geoearth.admin.mapper.dictionary;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.geoearth.admin.model.entity.dictionary.SysDictType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典类型数据访问接口。
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {
}
