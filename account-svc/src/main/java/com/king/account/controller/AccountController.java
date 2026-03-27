package com.king.account.controller;

import com.king.account.service.AccountService;
import com.king.staff.account.dto.*;
import com.king.staff.common.api.BaseResponse;
import com.king.staff.common.api.ResultCode;
import com.king.staff.common.auth.AuthConstant;
import com.king.staff.common.auth.Authorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/v1/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping(path = "/create")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE
    })
    public GenericAccountResponse createAccount(@RequestBody @Valid CreateAccountRequest request) {
        AccountDto accountDto = accountService.createAccount(request);
        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(accountDto);
        return response;
    }

    @PostMapping(path = "/track_event")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER
    })
    public BaseResponse trackEvent(@RequestBody @Valid TrackEventRequest request) {
        accountService.trackEvent(request);
        return BaseResponse.builder().message("event tracked").build();
    }

    @PostMapping(path = "/sync_user")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER
    })
    public BaseResponse syncUser(@RequestBody @Valid SyncUserRequest request) {
        accountService.syncUser(request);
        return BaseResponse.builder().message("user synced").build();
    }

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ListAccountResponse listAccounts(@RequestParam int offset, @RequestParam @Min(0) int limit) {
        AccountList accountList = accountService.listAccounts(offset, limit);
        ListAccountResponse response = new ListAccountResponse();
        response.setAccountList(accountList);
        return response;
    }

    @PostMapping(path = "/search")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ListAccountResponse searchAccounts(@RequestBody @Valid SearchAccountRequest request) {
        AccountList accountList = accountService.searchAccounts(request);
        ListAccountResponse response = new ListAccountResponse();
        response.setAccountList(accountList);
        return response;
    }

    @GetMapping(path = "/stats")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public AccountStatsResponse getAccountStats() {
        return accountService.getAccountStats();
    }

    @PostMapping(path = "/batch")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public BaseResponse batchOperation(@RequestBody @Valid BatchAccountRequest request) {
        return accountService.batchOperation(request);
    }

    @PostMapping(path = "/get_or_create")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE
    })
    public GenericAccountResponse getOrCreateAccount(@RequestBody @Valid GetOrCreateRequest request) {
        AccountDto accountDto = accountService.getOrCreateAccount(request);
        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(accountDto);
        return response;
    }

    @PostMapping(path = "/get")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE,
            AuthConstant.AUTHORIZATION_BOT_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER
    })
    public GenericAccountResponse getAccount(@RequestParam @NotBlank String userId) {
        AccountDto accountDto = accountService.getAccount(userId);
        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(accountDto);
        return response;
    }

    @PostMapping(path = "/update")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER
    })
    public GenericAccountResponse updateAccount(@RequestBody @Valid AccountDto accountDto) {
        AccountDto updatedAccountDto = accountService.updateAccount(accountDto);
        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(updatedAccountDto);
        return response;
    }

    @PostMapping(path = "/get_account_by_phonenumber")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE
    })
    public GenericAccountResponse getAccountByPhoneNumber(@RequestParam @NotBlank String phoneNumber) {
        AccountDto accountDto = accountService.getAccountByPhoneNumber(phoneNumber);
        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(accountDto);
        return response;
    }

    @PostMapping(path = "/update_password")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER
    })
    public BaseResponse updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        accountService.updatePassword(request);
        return BaseResponse.builder().message("password updated").build();
    }

    @PostMapping(path = "/verify_password")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public BaseResponse verifyPassword(@RequestBody @Valid VerifyPasswordRequest request) {
        accountService.verifyPassword(request);
        return BaseResponse.builder().message("password verified").build();
    }

    @PostMapping(path = "/request_password_reset")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public BaseResponse requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        accountService.requestPasswordReset(request);
        return BaseResponse.builder().message("password reset requested").build();
    }

    @PostMapping(path = "/request_email_change")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER
    })
    public BaseResponse requestEmailChange(@RequestBody @Valid EmailChangeRequest request) {
        accountService.requestEmailChange(request);
        return BaseResponse.builder().message("email change requested").build();
    }

    @PostMapping(path = "/change_email")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public BaseResponse changeEmail(@RequestBody @Valid EmailConfirmation request) {
        accountService.changeEmail(request);
        return BaseResponse.builder().message("email changed").build();
    }
}
