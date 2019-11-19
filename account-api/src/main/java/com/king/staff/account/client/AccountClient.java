package com.king.staff.account.client;

import com.king.staff.account.AccountConstant;
import org.springframework.cloud.openfeign.FeignClient;

//todo client side validation can be enabled as needed
@FeignClient(name= AccountConstant.SERVICE_NAME,path = "/v1/account",url = "${staffjoy.account-service-endpoint}")
public interface AccountClient {



}
