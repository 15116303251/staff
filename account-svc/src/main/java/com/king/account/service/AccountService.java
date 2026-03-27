package com.king.account.service;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;
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
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    static final ILogger logger = SLoggerFactory.getLogger(AccountService.class);

    private final AccountRepo accountRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EnvConfig envConfig;

    public AccountDto createAccount(CreateAccountRequest request) {
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

        Account account = Account.builder()
                .email(request.getEmail())
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .memberSince(Instant.now())
                .confirmedAndActive(false)
                .photoUrl(Helper.genernateGravatarUrl(request.getEmail()))
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
        logger.info("created account", auditLog);

        AccountDto accountDto = convertToDto(savedAccount);
        return accountDto;
    }

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

    public AccountList searchAccounts(SearchAccountRequest request) {
        if (request.getLimit() <= 0) {
            request.setLimit(10);
        }
        
        int page = request.getOffset() / request.getLimit();
        PageRequest pageRequest = PageRequest.of(page, request.getLimit());
        
        // In a real implementation, you would create a custom repository method
        // with dynamic query based on search criteria
        // For now, we'll implement a simple search using existing methods
        Page<Account> accountPage = accountRepo.findAll(pageRequest);
        
        // Filter results based on search criteria
        List<Account> filteredAccounts = accountPage.getContent().stream()
                .filter(account -> {
                    boolean matches = true;
                    
                    if (request.getName() != null && !request.getName().isEmpty()) {
                        matches = matches && account.getName() != null && 
                                 account.getName().toLowerCase().contains(request.getName().toLowerCase());
                    }
                    
                    if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                        matches = matches && account.getEmail() != null && 
                                 account.getEmail().toLowerCase().contains(request.getEmail().toLowerCase());
                    }
                    
                    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                        matches = matches && account.getPhoneNumber() != null && 
                                 account.getPhoneNumber().contains(request.getPhoneNumber());
                    }
                    
                    if (request.getConfirmedAndActive() != null) {
                        matches = matches && account.getConfirmedAndActive() == request.getConfirmedAndActive();
                    }
                    
                    return matches;
                })
                .collect(Collectors.toList());
        
        List<AccountDto> accountDtoList = filteredAccounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return AccountList.builder()
                .accounts(accountDtoList)
                .limit(request.getLimit())
                .offset(request.getOffset())
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
        logger.info("updated password", auditLog);
    }

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

    public AccountStatsResponse getAccountStats() {
        long totalAccounts = accountRepo.count();
        long activeAccounts = accountRepo.countByConfirmedAndActive(true);
        long inactiveAccounts = accountRepo.countByConfirmedAndActive(false);
        long accountsWithEmail = accountRepo.countByEmailIsNotNull();
        long accountsWithPhone = accountRepo.countByPhoneNumberIsNotNull();
        
        // Calculate accounts created in different time periods
        Instant now = Instant.now();
        Instant todayStart = now.minusSeconds(24 * 60 * 60);
        Instant weekStart = now.minusSeconds(7 * 24 * 60 * 60);
        Instant monthStart = now.minusSeconds(30 * 24 * 60 * 60);
        
        long accountsCreatedToday = accountRepo.countByMemberSinceAfter(todayStart);
        long accountsCreatedThisWeek = accountRepo.countByMemberSinceAfter(weekStart);
        long accountsCreatedThisMonth = accountRepo.countByMemberSinceAfter(monthStart);
        
        return AccountStatsResponse.builder()
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .inactiveAccounts(inactiveAccounts)
                .accountsWithEmail(accountsWithEmail)
                .accountsWithPhone(accountsWithPhone)
                .accountsCreatedToday(accountsCreatedToday)
                .accountsCreatedThisWeek(accountsCreatedThisWeek)
                .accountsCreatedThisMonth(accountsCreatedThisMonth)
                .build();
    }

    public BaseResponse batchOperation(BatchAccountRequest request) {
        int successCount = 0;
        int failCount = 0;
        
        for (String accountId : request.getAccountIds()) {
            try {
                Account account = accountRepo.findById(accountId)
                        .orElseThrow(() -> new ServiceException(ResultCode.NOT_FOUND));
                
                switch (request.getOperation()) {
                    case ACTIVATE:
                        account.setConfirmedAndActive(true);
                        accountRepo.save(account);
                        successCount++;
                        break;
                        
                    case DEACTIVATE:
                        account.setConfirmedAndActive(false);
                        accountRepo.save(account);
                        successCount++;
                        break;
                        
                    case DELETE:
                        accountRepo.delete(account);
                        successCount++;
                        break;
                        
                    case EXPORT:
                        // For export operation, we just count it as success
                        // In a real implementation, you would collect account data
                        successCount++;
                        break;
                        
                    default:
                        failCount++;
                        logger.warn("Unknown batch operation", "operation", request.getOperation(), "accountId", accountId);
                }
                
                // Log each operation
                LogEntry auditLog = LogEntry.builder()
                        .currentUserId(AuthContext.getUserId())
                        .authorization(AuthContext.getAuthz())
                        .targetType("account")
                        .targetId(accountId)
                        .updatedContents("batch operation: " + request.getOperation() + 
                                        (request.getReason() != null ? ", reason: " + request.getReason() : ""))
                        .build();
                logger.info("batch operation executed", auditLog);
                
            } catch (Exception ex) {
                failCount++;
                logger.error("Failed to process batch operation for account", 
                        "accountId", accountId, "operation", request.getOperation(), "error", ex.getMessage());
            }
        }
        
        String message = String.format("Batch operation completed. Success: %d, Failed: %d", successCount, failCount);
        return BaseResponse.builder()
                .message(message)
                .build();
    }

    private AccountDto convertToDto(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }

    private Account convertToModel(AccountDto accountDto) {
        return modelMapper.map(accountDto, Account.class);
    }
}
