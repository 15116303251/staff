package com.king.account.service;

import com.king.account.model.Account;
import com.king.account.repo.AccountRepo;
import com.king.staff.account.dto.*;
import com.king.staff.common.api.ResultCode;
import com.king.staff.common.auth.AuthContext;
import com.king.staff.common.env.EnvConfig;
import com.king.staff.common.error.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EnvConfig envConfig;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private AccountDto testAccountDto;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id("test-id")
                .name("Test User")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .memberSince(Instant.now())
                .confirmedAndActive(true)
                .support(false)
                .photoUrl("https://example.com/photo.jpg")
                .passwordHash("hashed-password")
                .build();

        testAccountDto = AccountDto.builder()
                .id("test-id")
                .name("Test User")
                .email("test@example.com")
                .phoneNumber("+1234567890")
                .memberSince(Instant.now())
                .confirmedAndActive(true)
                .support(false)
                .photoUrl("https://example.com/photo.jpg")
                .build();

        // Set up AuthContext for tests
        AuthContext.setAuthz("test-auth");
        AuthContext.setUserId("test-user-id");
    }

    @Test
    void createAccount_Success() {
        // Arrange
        CreateAccountRequest request = CreateAccountRequest.builder()
                .name("New User")
                .email("new@example.com")
                .phoneNumber("+1234567890")
                .build();

        when(accountRepo.findAccountByEmail(anyString())).thenReturn(null);
        when(accountRepo.findAccountByPhoneNumber(anyString())).thenReturn(null);
        when(accountRepo.save(any(Account.class))).thenReturn(testAccount);
        when(modelMapper.map(any(Account.class), eq(AccountDto.class))).thenReturn(testAccountDto);

        // Act
        AccountDto result = accountService.createAccount(request);

        // Assert
        assertNotNull(result);
        assertEquals(testAccountDto.getId(), result.getId());
        verify(accountRepo, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_EmailAlreadyExists() {
        // Arrange
        CreateAccountRequest request = CreateAccountRequest.builder()
                .name("New User")
                .email("existing@example.com")
                .build();

        when(accountRepo.findAccountByEmail("existing@example.com")).thenReturn(testAccount);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class,
                () -> accountService.createAccount(request));
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void getAccount_Success() {
        // Arrange
        when(accountRepo.findById("test-id")).thenReturn(Optional.of(testAccount));
        when(modelMapper.map(testAccount, AccountDto.class)).thenReturn(testAccountDto);

        // Act
        AccountDto result = accountService.getAccount("test-id");

        // Assert
        assertNotNull(result);
        assertEquals(testAccountDto.getId(), result.getId());
    }

    @Test
    void getAccount_NotFound() {
        // Arrange
        when(accountRepo.findById("non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class,
                () -> accountService.getAccount("non-existent"));
        assertEquals(ResultCode.NOT_FOUND, exception.getResultCode());
    }

    @Test
    void updatePassword_Success() {
        // Arrange
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .userId("test-id")
                .password("new-password")
                .build();

        when(accountRepo.findById("test-id")).thenReturn(Optional.of(testAccount));
        when(passwordEncoder.encode("new-password")).thenReturn("new-hashed-password");

        // Act
        accountService.updatePassword(request);

        // Assert
        assertEquals("new-hashed-password", testAccount.getPasswordHash());
        verify(accountRepo, times(1)).save(testAccount);
    }

    @Test
    void verifyPassword_Success() {
        // Arrange
        VerifyPasswordRequest request = VerifyPasswordRequest.builder()
                .email("test@example.com")
                .password("correct-password")
                .build();

        when(accountRepo.findAccountByEmail("test@example.com")).thenReturn(testAccount);
        when(passwordEncoder.matches("correct-password", "hashed-password")).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> accountService.verifyPassword(request));
    }

    @Test
    void verifyPassword_WrongPassword() {
        // Arrange
        VerifyPasswordRequest request = VerifyPasswordRequest.builder()
                .email("test@example.com")
                .password("wrong-password")
                .build();

        when(accountRepo.findAccountByEmail("test@example.com")).thenReturn(testAccount);
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class,
                () -> accountService.verifyPassword(request));
        assertEquals(ResultCode.UN_AUTHORIZED, exception.getResultCode());
    }
}