package ru.brill.service;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SenderNotEqualReceiverValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SenderNotEqualReceiver {

    String message() default "{При переводе не допускается, чтобы кошелек-отправитель и кошелек-получатель совпадали}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
