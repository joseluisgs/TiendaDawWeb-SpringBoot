package dev.joseluisgs.walaspringboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageValidator.class)
@Documented
public @interface ValidImage {
    String message() default "{validation.image.format}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
