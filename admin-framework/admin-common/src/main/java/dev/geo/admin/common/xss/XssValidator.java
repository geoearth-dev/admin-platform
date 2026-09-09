package dev.geo.admin.common.xss;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 自定义xss校验注解实现
 */
public class XssValidator implements ConstraintValidator<Xss, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isBlank(value)) {
            return true;
        }
        String cleanValue = HtmlUtil.cleanHtmlTag(value);
        return value.equals(cleanValue);
    }

}