package com.king.staff.account.dto;

import com.king.staff.common.api.BaseResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ListAccountResponse extends BaseResponse {

    private AccountList accountList;

}
