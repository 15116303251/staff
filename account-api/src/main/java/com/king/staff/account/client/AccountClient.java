package com.king.staff.account.client;

import com.king.staff.account.AccountConstant;
import com.king.staff.account.dto.*;
import com.king.staff.common.api.BaseResponse;
import com.king.staff.common.auth.AuthConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

//todo client side validation can be enabled as needed
@FeignClient(name= AccountConstant.SERVICE_NAME,path = "/v1/account",url = "${staffjoy.account-service-endpoint}")
public interface AccountClient {

    @PostMapping(path = "/create")
    GenericAccountResponse createAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz, @RequestBody @Valid CreateAccountRequest request);

    @PostMapping(path = "/track_event")
    BaseResponse trackEvent(@RequestBody @Valid TrackEventRequest request);

    @PostMapping(path = "/sync_user")
    BaseResponse syncUser(@RequestBody @Valid SyncUserRequest request);

    @GetMapping(path = "/list")
    ListAccountResponse listAccounts(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz, @RequestParam int offset,@RequestParam @Min(0) int limit );

    @PostMapping(path = "/get_or_create")
    GenericAccountResponse getOrCreateAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid GetOrCreateRequest request);

    @PostMapping(path = "/get")
    GenericAccountResponse getAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestParam @NotBlank String userId);

    @PostMapping(path="/update")
    GenericAccountResponse updateAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid AccountDto accountDto);

    @PostMapping(path = "/get_account_by_phonenumber")
    GenericAccountResponse getAccountByPhoneNumber(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestParam @Valid String phoneNumber);

    @PostMapping(path = "/update_password")
    BaseResponse updatePassword(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid UpdatePasswordRequest request);

    @PostMapping(path = "/verify_password")
    BaseResponse verifyPassword(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid VerifyPasswordRequest request);

    @PostMapping(path = "/request_password_reset")
    BaseResponse requestPasswordReset(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid PasswordResetRequest request);

    @PostMapping(path = "/request_email_change")
    BaseResponse requestEmailChange(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid EmailChangeRequest request);

    @PostMapping(path = "/change_email")
    BaseResponse changeEmail(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER)String authz,@RequestBody @Valid EmailConfirmation request);

}
