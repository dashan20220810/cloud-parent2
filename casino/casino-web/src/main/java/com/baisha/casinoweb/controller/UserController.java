package com.baisha.casinoweb.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.business.AssetsBusiness;
import com.baisha.casinoweb.business.UserBusiness;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.IpUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("user")
@Api(tags = { "用户控制器" })
@Slf4j
public class UserController {
	
	@Autowired
	private UserBusiness userBusiness;
	
	@Autowired
	private AssetsBusiness assetsBusiness;

	/**
	 * TG注册
	 * 
	 * @return
	 */
	@PostMapping("registerTG")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"), 
		@ApiImplicitParam(name = "nickname", value = "first name + last name (長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"),
		@ApiImplicitParam(name = "groupId", value = "telegram group id", dataType = "long", required = true, paramType = "query"),
		@ApiImplicitParam(name = "inviteTgUserId", value = "邀请人 tg_user_id", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "tgGroupName", value = "tg群名称", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "tgUserName", value = "tg玩家名称", dataType = "string", paramType = "query")
	})
	@ApiOperation("telegram注册")
	@NoAuthentication
	public ResponseEntity<?> registerTG(String id, String nickname, Long groupId, String inviteTgUserId, String tgGroupName, String tgUserName) {
		log.info("注册使用者");
		if ( CommonUtil.checkNull(id, nickname, tgGroupName) || groupId==null ) {
			log.info("注册检核失败");
			return ResponseUtil.parameterNotNull();
		}
		
		// 记录IP
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		if (!userBusiness.registerTG(ip, id, nickname, groupId, inviteTgUserId, tgGroupName, tgUserName)) {
            return ResponseUtil.fail();
		}

		log.info("注册成功");
        return ResponseUtil.success();
	}
	

	/**
	 * 查詢余額
	 * 
	 * @return
	 */
	@PostMapping("balance")
	@ApiOperation("查詢余額")
	public ResponseEntity<BigDecimal> balance () {

		log.info("查詢余額");
		BigDecimal balance = assetsBusiness.balance();
		if ( balance==null ) {
			log.info("查詢余額失敗");
            return ResponseUtil.fail();
		}

		log.info("查詢余額成功");
        return ResponseUtil.success(balance);
	}

	/**
	 * 用户登入
	 * @param userName
	 * @param password
	 * @return
	 */
//	@ApiImplicitParams({
//		@ApiImplicitParam(name = "userName", value = "用户名", dataType = "string", required = true, paramType = "query"),
//		@ApiImplicitParam(name = "password", value = "密码", dataType = "string", required = true, paramType = "query")
//	})
//	@ApiOperation("用户登入")
//	@NoAuthentication
//	@PostMapping("login")
//	public ResponseEntity<?> login( String userName, String password ) {
//		
//		log.info("[用户登入]");
//		if (CommonUtil.checkNull(userName, password)) {
//			log.info("[用户登入] 检核失败");
//			return ResponseUtil.parameterNotNull();
//		}
//		
//		
//		//USER_LOGIN
//		Map<String, Object> params = new HashMap<>();
//		params.put("userName", userName);
//		params.put("password", password);
//
//		String result = HttpClient4Util.doPost(
//				userServerDomain + RequestPathEnum.USER_LOGIN.getApiName(),
//				params);
//		
//        if (CommonUtil.checkNull(result)) {
//            return ResponseUtil.fail();
//        }
//
//		//TODO
//		log.info("[用户登入] 成功");
//        return ResponseUtil.success();
//	}
	
}
