package com.baisha.casinoweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.business.AssetsBusiness;
import com.baisha.casinoweb.business.OrderBusiness;
import com.baisha.casinoweb.business.UserBusiness;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.vo.BetVO;
import com.baisha.casinoweb.vo.UserVO;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.IpUtil;

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
	
	@Autowired
	private UserBusiness userBusiness;
	
	@Autowired
	private AssetsBusiness assetsBusiness;
	
	@Autowired
	private OrderBusiness orderBusiness;

    @PostMapping("bet")
    @ApiOperation("下注")
    @NoAuthentication
    public ResponseEntity<String> bet(BetVO betVO) {

		log.info("[下注]");
    	
    	if ( BetVO.checkRequest(betVO)==false ) {
    		log.info("[下注] 检核失败");
    		return ResponseUtil.fail();
    	}
    	
    	//  user id查user
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
            return ResponseUtil.fail();
    	}

    	//  呼叫
    	//	会员管理-下分api
    	if ( assetsBusiness.withdraw(userVO.getUserName(), betVO.getAmount())==false ) {
            return ResponseUtil.fail();
    	}
    	
		// 记录IP
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		//	TODO 輪/局號 應來自荷官端，不得從請求中代入
    	boolean betResult = orderBusiness.bet(ip, userVO.getId(), userVO.getUserName(), betVO.getBetOption(), betVO.getAmount(), "00001", "00001");
    	if ( betResult==false ) {
            return ResponseUtil.fail();
    	}

		log.info("[下注] 成功");
        return ResponseUtil.success();
    }
	
}
