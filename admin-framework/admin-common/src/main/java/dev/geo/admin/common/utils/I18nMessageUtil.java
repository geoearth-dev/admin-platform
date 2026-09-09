package dev.geo.admin.common.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nMessageUtil {
    private final MessageSourceAccessor accessor;

    public I18nMessageUtil(MessageSource messageSource) {
        this.accessor = new MessageSourceAccessor(messageSource);
    }

    public String get(String code, Object... args) {
        return get(code, args, LocaleContextHolder.getLocale());
    }

    public String get(String code, Object[] args, Locale locale) {
        return accessor.getMessage(code, args, code, locale);
    }

    public String getOrDefault(String code, String defaultMessage, Object... args) {
        return accessor.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }
}
