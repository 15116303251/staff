package com.king.staff.common.utils;

public class Helper {

    public static String genernateGravatarUrl(String email){
        String hash =MD5Util.md5Hex(email);
        return String.format("https://www.gravatar.com/avatar/%s.jpg?s=400&d=identicon",hash);
    }
}
