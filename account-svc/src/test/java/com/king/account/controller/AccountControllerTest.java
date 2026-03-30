package com.king.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.king.account.service.AccountService;
import com.king.staff.account.dto.*;
import com.king.staff.common.api.BaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_Success() throws Exception {
        // Arrange
        CreateAccountRequest request = CreateAccountRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .build();

        AccountDto accountDto = AccountDto.builder()
                .id("test-id")
                .name("Test User")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .memberSince(Instant.now())
                .confirmedAndActive(false)
                .support(false)
                .photoUrl("https://gravatar.com/avatar/test.jpg")
                .build();

        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(accountDto);

        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(accountDto);

        // Act & Assert
        mockMvc.perform(post("/v1/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id").value("test-id"))
                .andExpect(jsonPath("$.account.name").value("Test User"))
                .andExpect(jsonPath("$.account.email").value("test@example.com"));
    }

    @Test
    void getAccount_Success() throws Exception {
        // Arrange
        AccountDto accountDto = AccountDto.builder()
                .id("test-id")
                .name("Test User")
                .email("test@example.com")
                .memberSince(Instant.now())
                .confirmedAndActive(true)
                .support(false)
                .photoUrl("https://gravatar.com/avatar/test.jpg")
                .build();

        GenericAccountResponse response = new GenericAccountResponse();
        response.setAccount(accountDto);

        when(accountService.getAccount(anyString())).thenReturn(accountDto);

        // Act & Assert
        mockMvc.perform(get("/v1/account/get")
                        .param("userId", "test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id").value("test-id"))
                .andExpect(jsonPath("$.account.name").value("Test User"));
    }

    @Test
    void updateAccount_Success() throws Exception {
        // Arrange
        AccountDto requestDto = AccountDto.builder()
                .id("test-id")
                .name("Updated User")
                .email("updated@example.com")
                .memberSince(Instant.now())
                .confirmedAndActive(true)
                .support(false)
                .photoUrl("https://gravatar.com/avatar/updated.jpg")
                .build();

        when(accountService.updateAccount(any(AccountDto.class))).thenReturn(requestDto);

        // Act & Assert
        mockMvc.perform(post("/v1/account/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.id").value("test-id"))
                .andExpect(jsonPath("$.account.name").value("Updated User"))
                .andExpect(jsonPath("$.account.email").value("updated@example.com"));
    }

    @Test
    void listAccounts_Success() throws Exception {
        // Arrange
        AccountList accountList = AccountList.builder()
                .limit(10)
                .offset(0)
                .build();

        ListAccountResponse response = new ListAccountResponse();
        response.setAccountList(accountList);

        when(accountService.listAccounts(anyInt(), anyInt())).thenReturn(accountList);

        // Act & Assert
        mockMvc.perform(get("/v1/account/list")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountList.limit").value(10))
                .andExpect(jsonPath("$.accountList.offset").value(0));
    }

    @Test
    void updatePassword_Success() throws Exception {
        // Arrange
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .userId("test-id")
                .password("new-password")
                .build();

        when(accountService.updatePassword(any(UpdatePasswordRequest.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/v1/account/update_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("password updated"));
    }

    @Test
    void verifyPassword_Success() throws Exception {
        // Arrange
        VerifyPasswordRequest request = VerifyPasswordRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        when(accountService.verifyPassword(any(VerifyPasswordRequest.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/v1/account/verify_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("password verified"));
    }
}