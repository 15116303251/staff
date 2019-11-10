package com.king.staff.common.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DayOfWeekValidator.class)
@Target({ElementType.FIELD})
public @interface DayOfWeek {

    String message() default "Unknown day of week";
    Class[] groups() default {};
    Class[] payload()default {};
}
