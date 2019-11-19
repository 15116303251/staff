package com.king.staff.account.dto;

import com.king.staff.common.validation.PhoneNumber;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;

public class GetOrCreateRequest {

    private String name;
    @Email(message = "Invalid email")
    private String email;
    @PhoneNumber
    private String phoneNumber;

    @AssertTrue(message = "Empty request")
    private boolean isValidRequest(){
        return StringUtils.hasText(name) || StringUtils.hasText(email) ||StringUtils.hasText(phoneNumber);
    }

}
