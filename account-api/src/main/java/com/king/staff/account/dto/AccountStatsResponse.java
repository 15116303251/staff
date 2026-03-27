package com.king.staff.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountStatsResponse {
    
    private long totalAccounts;
    
    private long activeAccounts;
    
    private long inactiveAccounts;
    
    private long accountsWithEmail;
    
    private long accountsWithPhone;
    
    private long accountsCreatedToday;
    
    private long accountsCreatedThisWeek;
    
    private long accountsCreatedThisMonth;
}