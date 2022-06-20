package com.baisha.casinoweb.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.constant.Constants;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.vo.BetVO;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulecommon.util.SnowFlakeUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@RestController
@RequestMapping("order")
@Api(tags = { "订单管理" })
@Slf4j
public class OrderController {

	@Value("${project.server-url.game-server-domain}")
	private String gameServerDomain;

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;

    @PostMapping("bet")
    @ApiOperation("下注")
    @NoAuthentication
    public ResponseEntity<String> bet(BetVO betVO) {

		log.info("[下注]");
    	
    	if ( BetVO.checkRequest(betVO)==false ) {
    		log.info("[下注] 检核失败");
    		return ResponseUtil.fail();
    	}

    	//  呼叫
    	//	会员管理-下分api
    	Map<String, Object> params2 = new HashMap<>();
    	params2.put("userName", betVO.getUserName());
    	params2.put("amount", betVO.getAmount());
    	params2.put("balanceType", Constants.BALANCE_TYPE_WITHDRAW);
    	params2.put("remark", "下注");

    	String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.ASSETS_BALANCE.getApiName(),
				params2);
		
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }

		JSONObject balanceJson = JSONObject.parseObject(result);
		Integer code = balanceJson.getInteger("code");

		if ( code!=0 ) {
            return ResponseUtil.fail();
		}
    	
		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		
		params.put("clientIP", ip);
		// TODO 來自token解析
		// tg有可能沒有token，必須直接從bot取得user id
//		params.put("userId", );  
		params.put("betOption", betVO.getBetOption());
		params.put("amount", betVO.getAmount());
		
		//	TODO 輪/局號 應來自荷官端，不得從請求中代入
		params.put("noRun", "00001");
		params.put("noActive", "00001");
		params.put("status", 1);
		params.put("orderNo", SnowFlakeUtils.getSnowId());

		result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_BET.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		code = betJson.getInteger("code");

		if ( code!=0 ) {
            return ResponseUtil.fail();
		}

		log.debug("==== ORDER_BET ==== \r\nreponse: {}", result);
		log.info("[下注] 成功");
        return ResponseUtil.success();
    }
	
}
