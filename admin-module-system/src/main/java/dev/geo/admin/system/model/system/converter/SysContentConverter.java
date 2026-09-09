package dev.geo.admin.system.model.system.converter;

import dev.geo.admin.system.model.system.dto.SysContentSaveReqDTO;
import dev.geo.admin.system.model.system.entity.SysContent;
import dev.geo.admin.system.model.system.vo.SysContentPublicVO;
import dev.geo.admin.system.model.system.vo.SysContentRespVO;

/**
 * 系统展示配置对象转换器。
 */
public final class SysContentConverter {

    private SysContentConverter() {
    }

    /**
     * 将实体转换为后台响应对象。
     */
    public static SysContentRespVO toRespVO(SysContent data) {
        if (data == null) {
            return null;
        }
        return SysContentRespVO.builder()
                .id(data.getId())
                .sysName(data.getSysName())
                .loginLogo(data.getLoginLogo())
                .logo(data.getLogo())
                .carouselImage(data.getCarouselImage())
                .contactNumber(data.getContactNumber())
                .email(data.getEmail())
                .copyright(data.getCopyright())
                .recordNumber(data.getRecordNumber())
                .delFlag(data.getDelFlag())
                .status(data.getStatus())
                .creatorId(data.getCreatorId())
                .createBy(data.getCreateBy())
                .createTime(data.getCreateTime())
                .updaterId(data.getUpdaterId())
                .updateBy(data.getUpdateBy())
                .updateTime(data.getUpdateTime())
                .remark(data.getRemark())
                .build();
    }

    /**
     * 将实体转换为匿名页面使用的公开响应对象。
     */
    public static SysContentPublicVO toPublicVO(SysContent data) {
        if (data == null) {
            return null;
        }
        SysContentPublicVO result = new SysContentPublicVO();
        result.setId(data.getId());
        result.setSysName(data.getSysName());
        result.setLoginLogo(data.getLoginLogo());
        result.setLogo(data.getLogo());
        result.setCarouselImage(data.getCarouselImage());
        result.setContactNumber(data.getContactNumber());
        result.setEmail(data.getEmail());
        result.setCopyright(data.getCopyright());
        result.setRecordNumber(data.getRecordNumber());
        result.setStatus(data.getStatus());
        return result;
    }


}
