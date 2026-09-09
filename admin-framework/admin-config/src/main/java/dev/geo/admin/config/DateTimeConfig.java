package dev.geo.admin.config;

import dev.geo.admin.common.constant.DateTimeFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration(proxyBeanMethods = false)
public class DateTimeConfig {
    @Bean
    public JacksonModule dateTimeModule() {
        SimpleModule module = new SimpleModule();

        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormat.DATE));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormat.DATE));

        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormat.DATE_TIME));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormat.DATE_TIME));

        return module;
    }
}
