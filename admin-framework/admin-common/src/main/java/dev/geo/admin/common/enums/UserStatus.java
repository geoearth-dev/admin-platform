package dev.geo.admin.common.enums;

import lombok.Getter;

/**
 * 用户状态
 * 
 */
@Getter
public enum UserStatus
{
    OK("1", "正常"), DISABLE("0", "停用"), DELETED("2", "删除");

    private final String code;
    private final String info;

    UserStatus(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

}
