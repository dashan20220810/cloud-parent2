package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.model.vo.UserVO;
import com.baisha.modulecommon.enums.UserOriginEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

@Component
public class UserBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;
	
	@Value("${project.telegram.register-password}")
	private String tgRegisterPassword;
	
	public UserVO getUserVO( String userIdOrName ) {
		return getUserVO( false, userIdOrName );
	}
	
	public UserVO getUserVO( boolean isTelegramRequest, String userId ) {

    	//  user id查user
    	UserVO userVO = null;
    	
    	if ( isTelegramRequest ) {
        	userVO = CasinoWebUtil.getUserVO(userServerDomain, userId);
			//	token中查无user资料
		} else {
        	userVO = CasinoWebUtil.getUserVO(userServerDomain, Long.parseLong(userId));
			//	token中查无user资料
		}
    	
    	return userVO;
	}

	public boolean registerTG( String clientIP, String id, String nickName, Long groupId
			, String inviteTgUserId, String tgGroupName, String tgUserName ) {

		Map<String, Object> params = new HashMap<>();
		params.put("ip", clientIP);
		params.put("tgUserId", id);
		params.put("tgGroupId", groupId);
		params.put("nickName", nickName);
		params.put("inviteTgUserId", inviteTgUserId);
		params.put("tgGroupName", tgGroupName);
		params.put("tgUserName", tgUserName);
		params.put("password", tgRegisterPassword);
		params.put("origin", UserOriginEnum.TG_ORIGIN.getOrigin());

		String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.USER_REGISTER.getApiName(),
				params);
		
        if (CommonUtil.checkNull(result)) {
            return false;
        }
        
		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");

		return code != null && code == 0;
	}
	
}
