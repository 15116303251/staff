package com.king.staff.common.services;

public class SecurityConstant {

    //public security means a user may be logged out or logged in
    public static final int SEC_PUBLIC=0;

    //Authenticated security means a user must be logged in
    public static final int SEC_AUTHENTICATED=1;

    //admin security means a user must be both logged in and hava sudo flag
    public static final int SEC_ADMIN=2;
}
