package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.vo.UserVO;
import com.baisha.modulecommon.reponse.ResponseUtil;
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
	
	public UserVO getUserVO( boolean isTelegramRequest, String userIdOrName ) {

    	//  user id查user
    	String userName = null;
    	Long userId = null;
    	UserVO userVO = null;
    	
    	if ( isTelegramRequest ) {
    		userName = userIdOrName;
        	userVO = CasinoWebUtil.getUserVO(userServerDomain, userName);
        	if ( userVO!=null ) {
        		userId = userVO.getId();
        	} else {
        		//	token中查无user资料
                return null;
        	}
    	} else {
    		userId = Long.parseLong(userIdOrName);
        	userVO = CasinoWebUtil.getUserVO(userServerDomain, userId);
        	if ( userVO!=null ) {
        		userName = userVO.getUserName();
        	} else {
        		//	token中查无user资料
                return null;
        	}
    	}
    	
    	return userVO;
	}

	public boolean registerTG( String clientIP, String userName, String nickName ) {

		Map<String, Object> params = new HashMap<>();
		params.put("ip", clientIP);
		params.put("userName", userName);
		params.put("nickName", nickName);
		params.put("password", tgRegisterPassword);

		String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.USER_REGISTER.getApiName(),
				params);
		
        if (CommonUtil.checkNull(result)) {
            return false;
        }
        
        return true;
	}
	
}
