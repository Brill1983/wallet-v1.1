package ru.brill.service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameConstraint {
    String message() default "Имя или фамилия не соответствуют требованиям";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
