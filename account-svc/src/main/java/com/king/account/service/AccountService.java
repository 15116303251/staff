package com.king.account.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.king.account.model.Account;
import com.king.account.repo.AccountRepo;
import com.king.staff.account.AccountConstant;
import com.king.staff.account.dto.*;
import com.king.staff.common.api.BaseResponse;
import com.king.staff.common.api.ResultCode;
import com.king.staff.common.auditlog.LogEntry;
import com.king.staff.common.auth.AuthConstant;
import com.king.staff.common.auth.AuthContext;
import com.king.staff.common.crypto.Sign;
import com.king.staff.common.env.EnvConfig;
import com.king.staff.common.error.ServiceException;
import com.king.staff.common.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Account Service - Handles business logic for user account management.
 * Provides functionality for creating, retrieving, updating, and deleting user accounts.
 * Includes password management, account verification, and listing operations.
 * 
 * @author King Staff
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepo accountRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EnvConfig envConfig;

    /**
     * Creates a new user account.
     * Validates that the email or phone number is provided and not already in use.
     * Generates a Gravatar URL for the profile photo if email is provided.
     * 
     * @param request The account creation request containing user details
     * @return AccountDto containing the created account information
     * @throws ServiceException if email or phone number already exists, or if neither is provided
     */
    public AccountDto createAccount(CreateAccountRequest request) {
        // Validate that at least one of email or phone number is provided
        if (!StringUtils.hasText(request.getEmail()) && !StringUtils.hasText(request.getPhoneNumber())) {
            throw new ServiceException("Either email or phone number must be provided");
        }

        // check if account already exists by email
        if (StringUtils.hasText(request.getEmail())) {
            Account existingAccount = accountRepo.findAccountByEmail(request.getEmail());
            if (existingAccount != null) {
                throw new ServiceException("A user with that email already exists");
            }
        }

        // check if account already exists by phone number
        if (StringUtils.hasText(request.getPhoneNumber())) {
            Account existingAccount = accountRepo.findAccountByPhoneNumber(request.getPhoneNumber());
            if (existingAccount != null) {
                throw new ServiceException("A user with that phone number already exists");
            }
        }

        // Generate photo URL - use email if available, otherwise use a default
        String photoUrl;
        if (StringUtils.hasText(request.getEmail())) {
            photoUrl = Helper.generateGravatarUrl(request.getEmail());
        } else {
            // Use a default avatar for phone-only accounts
            photoUrl = "https://www.gravatar.com/avatar/00000000000000000000000000000000.jpg?s=400&d=identicon";
        }

        Account account = Account.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .memberSince(Instant.now())
                .confirmedAndActive(false)
                .photoUrl(photoUrl)
                .build();

        Account savedAccount;
        try {
            savedAccount = accountRepo.save(account);
        } catch (Exception ex) {
            String errMsg = "Could not create account";
            logger.error(errMsg, ex);
            throw new ServiceException(errMsg, ex);
        }

        LogEntry auditLog = LogEntry.builder()
                .currentUserId(AuthContext.getUserId())
                .authorization(AuthContext.getAuthz())
                .targetType("account")
                .targetId(savedAccount.getId())
                .updatedContents(savedAccount.toString())
                .build();
        logger.info("created account: {}", auditLog);

        AccountDto accountDto = convertToDto(savedAccount);
        return accountDto;
    }

    /**
     * Retrieves an account by user ID.
     * 
     * @param userId The unique identifier of the user account
     * @return AccountDto containing the account information
     * @throws ServiceException if the account is not found
     */
    public AccountDto getAccount(String userId) {
        Account account = accountRepo.findById(userId)
                .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));
        return convertToDto(account);
    }

    public AccountDto getAccountByPhoneNumber(String phoneNumber) {
        Account account = accountRepo.findAccountByPhoneNumber(phoneNumber);
        if (account == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }
        return convertToDto(account);
    }

    public AccountDto updateAccount(AccountDto accountDto) {
        Account existingAccount = accountRepo.findById(accountDto.getId())
                .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));

        Account accountToUpdate = convertToModel(accountDto);
        accountToUpdate.setPasswordHash(existingAccount.getPasswordHash());

        try {
            accountRepo.save(accountToUpdate);
        } catch (Exception ex) {
            String errMsg = "Could not update the account";
            logger.error(errMsg, ex);
            throw new ServiceException(errMsg, ex);
        }

        LogEntry auditLog = LogEntry.builder()
                .currentUserId(AuthContext.getUserId())
                .authorization(AuthContext.getAuthz())
                .targetType("account")
                .targetId(accountDto.getId())
                .originalContents(existingAccount.toString())
                .updatedContents(accountToUpdate.toString())
                .build();
        logger.info("updated account", auditLog);

        AccountDto updatedAccountDto = convertToDto(accountToUpdate);
        return updatedAccountDto;
    }

    public AccountList listAccounts(int offset, int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        int page = offset / limit;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<Account> accountPage = accountRepo.findAll(pageRequest);
        List<AccountDto> accountDtoList = accountPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return AccountList.builder()
                .accounts(accountDtoList)
                .limit(limit)
                .offset(offset)
                .build();
    }

    public AccountDto getOrCreateAccount(GetOrCreateRequest request) {
        // try to find existing account by email
        if (StringUtils.hasText(request.getEmail())) {
            Account existingAccount = accountRepo.findAccountByEmail(request.getEmail());
            if (existingAccount != null) {
                return convertToDto(existingAccount);
            }
        }

        // try to find existing account by phone number
        if (StringUtils.hasText(request.getPhoneNumber())) {
            Account existingAccount = accountRepo.findAccountByPhoneNumber(request.getPhoneNumber());
            if (existingAccount != null) {
                return convertToDto(existingAccount);
            }
        }

        // not found, create a new account
        CreateAccountRequest createRequest = CreateAccountRequest.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();
        return createAccount(createRequest);
    }

    /**
     * Updates the password for a user account.
     * 
     * @param request The password update request containing user ID and new password
     * @throws ServiceException if the account is not found
     */
    public void updatePassword(UpdatePasswordRequest request) {
        Account account = accountRepo.findById(request.getUserId())
                .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));

        String passwordHash = passwordEncoder.encode(request.getPassword());
        account.setPasswordHash(passwordHash);

        try {
            accountRepo.save(account);
        } catch (Exception ex) {
            String errMsg = "Could not update password";
            logger.error(errMsg, ex);
            throw new ServiceException(errMsg, ex);
        }

        LogEntry auditLog = LogEntry.builder()
                .currentUserId(AuthContext.getUserId())
                .authorization(AuthContext.getAuthz())
                .targetType("account")
                .targetId(request.getUserId())
                .updatedContents("password updated")
                .build();
        logger.info("updated password: {}", auditLog);
    }

    /**
     * Verifies a user's password.
     * 
     * @param request The password verification request containing email and password
     * @throws ServiceException if the account is not found or password is incorrect
     */
    public void verifyPassword(VerifyPasswordRequest request) {
        Account account = accountRepo.findAccountByEmail(request.getEmail());
        if (account == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        if (!StringUtils.hasText(account.getPasswordHash())) {
            throw new ServiceException(ResultCode.REQ_REJECT);
        }

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new ServiceException(ResultCode.UN_AUTHORIZED);
        }
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        Account account = accountRepo.findAccountByEmail(request.getEmail());
        if (account == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        String token = Sign.generateEmailConfirmationToken(account.getId(), account.getEmail(), envConfig.getExternalApex());
        String resetLink = String.format("%s://%s/reset/%s", envConfig.getScheme(), envConfig.getExternalApex(), token);

        String htmlBody = String.format(AccountConstant.RESET_PASSWORD_TMPL, resetLink, resetLink);

        // In a real system, send email here
        logger.info("Sending password reset email", "email", account.getEmail(), "resetLink", resetLink);
    }

    public void requestEmailChange(EmailChangeRequest request) {
        Account account = accountRepo.findById(request.getUserId())
                .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));

        // check if email is already in use
        Account existingAccount = accountRepo.findAccountByEmail(request.getEmail());
        if (existingAccount != null) {
            throw new ServiceException("A user with that email already exists");
        }

        String token = Sign.generateEmailConfirmationToken(account.getId(), request.getEmail(), envConfig.getExternalApex());
        String confirmLink = String.format("%s://%s/confirm/%s", envConfig.getScheme(), envConfig.getExternalApex(), token);

        String htmlBody = String.format(AccountConstant.CONFIRM_EMAIL_TMPL, account.getName(), confirmLink, confirmLink, confirmLink);

        // In a real system, send email here
        logger.info("Sending email change confirmation", "email", request.getEmail(), "confirmLink", confirmLink);
    }

    public void changeEmail(EmailConfirmation request) {
        Account account = accountRepo.findById(request.getUserId())
                .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));

        account.setEmail(request.getEmail());
        account.setConfirmedAndActive(true);

        try {
            accountRepo.save(account);
        } catch (Exception ex) {
            String errMsg = "Could not change email";
            logger.error(errMsg, ex);
            throw new ServiceException(errMsg, ex);
        }

        LogEntry auditLog = LogEntry.builder()
                .currentUserId(AuthContext.getUserId())
                .authorization(AuthContext.getAuthz())
                .targetType("account")
                .targetId(request.getUserId())
                .updatedContents("email changed to " + request.getEmail())
                .build();
        logger.info("changed email", auditLog);
    }

    public void trackEvent(TrackEventRequest request) {
        logger.info("track event", "userId", request.getUserId(), "event", request.getEvent());
    }

    public void syncUser(SyncUserRequest request) {
        Account account = accountRepo.findById(request.getUserId())
                .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));
        logger.info("sync user", "userId", request.getUserId());
    }

    private AccountDto convertToDto(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }

    private Account convertToModel(AccountDto accountDto) {
        return modelMapper.map(accountDto, Account.class);
    }
}
