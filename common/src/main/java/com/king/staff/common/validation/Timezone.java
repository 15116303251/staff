package com.king.staff.common.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimezoneValidator.class)
@Target({ElementType.FIELD})
public @interface Timezone {

    String message() default "Invalid Timezone";
    Class[] groups() default {};
    Class[] payload()default {};
}
