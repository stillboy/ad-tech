package com.deali.adtech.infrastructure.util.annotation;

import com.deali.adtech.infrastructure.util.valid.ImageValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface Image {
    String message() default "제한 용량을 초과하셨습니다.";
}
