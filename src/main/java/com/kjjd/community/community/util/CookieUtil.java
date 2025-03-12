package com.kjjd.community.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static String getValue(HttpServletRequest response, String name){
        if(response==null||name==null)
        {
            throw new IllegalArgumentException();
        }
        Cookie[] cookies = response.getCookies();
        if(cookies!=null)
        for(Cookie cookie:cookies)
        {
            if(cookie.getName().equals(name))
            {
                return cookie.getValue();
            }
        }
        return null;
    }


}
