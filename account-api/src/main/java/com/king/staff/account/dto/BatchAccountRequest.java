package com.king.staff.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchAccountRequest {
    
    public enum Operation {
        ACTIVATE,
        DEACTIVATE,
        DELETE,
        EXPORT
    }
    
    @NotNull
    private Operation operation;
    
    @NotEmpty
    private List<String> accountIds;
    
    private String reason;
}