package com.king.staff.account.dto;

import com.king.staff.common.api.BaseResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class GenericAccountResponse extends BaseResponse {

    private AccountDto account;
}
