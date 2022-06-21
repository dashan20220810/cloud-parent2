package com.baisha.casinoweb.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.vo.UserVO;

@Component
public class UserBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;
	
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

}
