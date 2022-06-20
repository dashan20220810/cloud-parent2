package com.baisha.casinoweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
@Api(tags = { "用户控制器" })
@Slf4j
public class UserController {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;
	
	@Value("${project.telegram.register-password")
	private String tgRegisterPassword;

	/**
	 * TG注册
	 * 
	 * @return
	 */
	@PostMapping("registerTG")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "nickname", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"),
			@ApiImplicitParam(name = "name", value = "用户名(長度3-20,只能輸入_,字母,數字)", dataType = "string", required = true, paramType = "query"), })
	@ApiOperation("telegram注册")
	@NoAuthentication
	public ResponseEntity<?> registerTG(String name, String nickname) {
		log.info("注册使用者: %s", name);
		if (CommonUtil.checkNull(name, nickname)) {
			log.info("注册检核失败");
			return ResponseUtil.parameterNotNull();
		}

		// 记录IP
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());

		Map<String, Object> params = new HashMap<>();
		params.put("ip", ip);
		params.put("userName", name);
		params.put("nickName", nickname);
		params.put("password", tgRegisterPassword);

		String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.USER_REGISTER.getApiName(),
				params);
		
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }

		log.debug("==== BACKEND_REGISTER_USER ==== \r\nreponse: %s", result);
		log.info("注册成功");
		return JSONObject.parseObject(result, ResponseEntity.class);
	}

}
