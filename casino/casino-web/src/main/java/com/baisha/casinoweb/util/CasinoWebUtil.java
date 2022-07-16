package com.baisha.casinoweb.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.enums.UserOriginEnum;
import com.baisha.modulejjwt.JjwtUtil;

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

    public static boolean isTelegramRequest() {
        String tg = getRequest().getHeader(UserOriginEnum.TG_ORIGIN.getOrigin());
        if (StringUtils.isNotEmpty(tg)) {
            return true;
        }

        return false;
    }
    public static String getCurrentUserId() {
        String token = getToken();
        JjwtUtil.Subject subject = JjwtUtil.getSubject(token);
        if (subject == null || ObjectUtils.isEmpty(subject.getUserId())) {
            return null;
        }
        return subject.getUserId();
    }

}
