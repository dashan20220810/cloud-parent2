package com.baisha.casinoweb.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.casinoweb.vo.UserVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.enums.UserOriginEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
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

    public static UserVO getUserVO( String userServerDomain, Long userId ) {

    	String params = "?userId=" + userId;

		return getUserVO(userServerDomain, RequestPathEnum.USER_QUERY_BY_ID.getApiName(), 
				params);
    }

    public static UserVO getUserVO( String userServerDomain, String tgUserId, Long tgGroupId ) {

    	String params = "?tgUserId=" + tgUserId +"&tgGroupId=" +tgGroupId;

		return getUserVO(userServerDomain, RequestPathEnum.USER_QUERY_BY_USER_NAME.getApiName(), 
				params);
    }
    
    private static UserVO getUserVO( String userServerDomain, String api, String params ) {

    	String result = HttpClient4Util.doGet(userServerDomain + api + params);
        if (CommonUtil.checkNull(result)) {
            return null;
        }

		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");
		if ( code==null || code!=0 ) {
			return null;
		}

		JSONObject data = json.getJSONObject("data");
		UserVO vo = new UserVO();
		vo.setId(data.getLong("id"));
		vo.setNickName(data.getString("nickName"));
		vo.setUserName(data.getString("userName"));
		return vo;
    }
}
