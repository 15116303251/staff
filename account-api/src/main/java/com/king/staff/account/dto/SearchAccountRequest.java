package com.king.staff.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAccountRequest {
    
    private String name;
    
    private String email;
    
    private String phoneNumber;
    
    @Builder.Default
    @Min(0)
    private int offset = 0;
    
    @Builder.Default
    @Min(1)
    private int limit = 10;
    
    private Boolean confirmedAndActive;
    
    private String sortBy;
    
    private String sortOrder;
}