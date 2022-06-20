package com.baisha.casinoweb.util;

import cn.hutool.core.util.StrUtil;
import com.baisha.modulecommon.Constants;
import com.baisha.modulejjwt.JjwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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
        String tg = getRequest().getHeader(Constants.TELEGRAM);
        if (StringUtils.isNotEmpty(tg)) {
            return true;
        }

        return false;
    }
    public static Long getCurrentUserId() {
        String token = getToken();
        JjwtUtil.Subject subject = JjwtUtil.getSubject(token);
        if (subject == null || ObjectUtils.isEmpty(subject.getUserId())) {
            return null;
        }
        //获取登陆用户ID
        Long authId = Long.parseLong(subject.getUserId());
        return authId;
    }

    public static Pageable setPageable(Integer pageCode, Integer pageSize, Sort sort) {
        if (Objects.isNull(sort)) {
            sort = Sort.unsorted();
        }

        if (pageSize == null || pageCode == null) {
            pageCode = 1;
            pageSize = 10;
        }

        if (pageCode < 1 || pageSize < 1) {
            pageCode = 1;
            pageSize = 10;
        }

        if (pageSize > 100) {
            pageSize = 100;
        }

        Pageable pageable = PageRequest.of(pageCode - 1, pageSize, sort);
        return pageable;
    }

    public static Pageable setPageable(Integer pageCode, Integer pageSize) {
        return setPageable(pageCode, pageSize, null);
    }


}
