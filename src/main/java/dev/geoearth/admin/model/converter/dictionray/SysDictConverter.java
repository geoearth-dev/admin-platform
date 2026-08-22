package dev.geoearth.admin.model.converter.dictionray;

import dev.geoearth.admin.model.entity.dictionary.SysDictData;
import dev.geoearth.admin.model.vo.dictionary.DictOptionVO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SysDictConverter {
    public static DictOptionVO toVO(SysDictData data) {
        if (data == null) {
            return null;
        }
        return DictOptionVO.builder()
                .label(data.getDictLabel())
                .value(data.getDictValue())
                .build();
    }
}