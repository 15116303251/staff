package com.king.staff.common.auth;

/**
 * @author 旺
 * @description 权限相关的常量
 * @date 2019-11-02
 */
public class AuthConstant {

    public static final String COOKIE_NAME="staff-faraday";
    //header set for internal user id
    public static final String CURRENT_USER_HEADER="faraday-current-user-id";
    //AUTHORIZATION_HEADER is the http request header
    //key used for accessing the internal authorization
    public static final String AUTHORIZATION_HEADER="Authorization";
    //AUTHORIZATION_ANONYMOUS_WEB is set as the Authorization header to denote that
    //the request is unauthenticated
    public static final String AUTHORIZATION_ANONYMOUS_WEB="faraday-anonymous";
    //AUTHORIZATION_COMPANY_SERVICE is set as the Authorization header to denote
    //that a request is being made by the company service
    public static final String AUTHORIZATION_COMPANY_SERVICE="company-service";
    //AUTHORIZATION_BOT_SERVICE is set as the Authorization header to denote
    //that a request is being made by the bot microservice
    public static final String AUTHORIZATION_BOT_SERVICE="bot-service";
    //AUTHORIZATION_ACCOUNT_SERVICE is set as the Authorization header to denote
    //that a request is being made by the account service
    public static final String AUTHORIZATION_ACCOUNT_SERVICE="account-service";
    //AUTHORIZATION_SUPPORT_USER is set as the Authorization header to denote
    //that a request is being made by a staff team member
    public static final String AUTHORIZATION_SUPPORT_USER="faraday-support";
    //AUTHORIZATION_SUPERPOWERS_SERVICE is set as the Authorization header to denote
    //that a request is being made by the dev-only superpower service
    public static final String AUTHORIZATION_SUPERPOWERS_SERVICE="superpowers-service";
    //AUTHORIZATION_WWW_SERVICE is set as the Authorization header to denote
    //that a request is being made by the www login /signup system
    public static final String AUTHORIZATION_WWW_SERVICE="www-service";
    //AUTHORIZATION_WHOAMI_SERVICE is set as the Authorization header to denote
    //that a request is being made by the whoami microservice
    public static final String AUTHORIZATION_WHOAMI_SERVICE="whoami-service";
    //AUTHORIZATION_AUTHENTICATED_USER is set as the Authorization header to denote
    //that a request is being made by an authenticated we6b user
    public static final String AUTHORIZATION_AUTHENTICATED_USER ="faraday-authenticated";
    //AUTHORIZATION_ICAL_SERVICE is set as the Authorization header to denote
    //that a request is being made by the ical service
    public static final String AUTHORIZATION_ICAL_SERVICE="ical-service";
    //AUTH error messages
    public static final String ERROR_MSG_DO_NOT_HAVE_ACCESS="you don't have access to this service";
    public static final String ERROR_MSG_MISSING_AUTH_HEADER="missing authorization http header";

}
