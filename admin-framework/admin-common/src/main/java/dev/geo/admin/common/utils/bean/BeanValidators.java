package dev.geo.admin.common.utils.bean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;

/**
 * bean对象属性验证
 */
public class BeanValidators {
    public static <T> void validateWithException(Validator validator, T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
