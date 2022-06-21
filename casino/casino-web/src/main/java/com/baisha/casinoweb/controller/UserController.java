package com.baisha.casinoweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	/**
	 * TG注册
	 * 
	 * @return
	 */
	@PostMapping("registerTG")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "nickname", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"),
			@ApiImplicitParam(name = "name", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"), 
			@ApiImplicitParam(name = "groupId", value = "telegram group id", dataType = "long", required = true, paramType = "query")})
	@ApiOperation("telegram注册")
	@NoAuthentication
	public ResponseEntity<?> registerTG(String name, String nickname, Long groupId) {
		log.info("注册使用者");
		if (CommonUtil.checkNull(name, nickname) || groupId==null) {
			log.info("注册检核失败");
			return ResponseUtil.parameterNotNull();
		}
		
		// 记录IP
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		if ( userBusiness.registerTG(ip, name, nickname)==false ) {
            return ResponseUtil.fail();
		}

		log.info("注册成功");
        return ResponseUtil.success();
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
