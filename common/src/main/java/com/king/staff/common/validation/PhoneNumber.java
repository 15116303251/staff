package com.king.staff.common.validation;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface PhoneNumber {

    String message() default "Invalid phone number";
    Class[] groups() default {};
    Class[] payload()default {};
}
