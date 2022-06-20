package com.baisha.casinoweb.util;

import com.baisha.modulecommon.Constants;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class CasinoWebUtil {

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String getToken() {
        String token = getRequest().getHeader(Constants.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(token)) {
            token = token.replaceAll("Bearer ", "");
        }
        return token;
    }

    public static String getToken(String token) {
        if (!ObjectUtils.isEmpty(token)) {
            token = token.replaceAll("Bearer ", "");
        }
        return token;
    }
}
