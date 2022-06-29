package com.baisha.casinoweb.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.business.AssetsBusiness;
import com.baisha.casinoweb.business.DeskBusiness;
import com.baisha.casinoweb.business.GameInfoBusiness;
import com.baisha.casinoweb.business.OrderBusiness;
import com.baisha.casinoweb.business.UserBusiness;
import com.baisha.casinoweb.model.vo.BetVO;
import com.baisha.casinoweb.model.vo.UserVO;
import com.baisha.casinoweb.model.vo.response.BetResponseVO;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.GameTgGroupInfo;

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
	
	@Autowired
	private DeskBusiness deskBusiness;
	
	@Autowired 
	private GameInfoBusiness gameInfoBusiness;

    @PostMapping("bet")
    @ApiOperation("下注")
    public ResponseEntity<String> bet(BetVO betVO) {

		log.info("[下注]");
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	
    	if ( BetVO.checkRequest(betVO)==false 
    			|| (isTgRequest && betVO.getTgChatId()==null) ) {
    		log.info("[下注] 检核失败");
    		return ResponseUtil.fail();
    	}
    	
    	// TODO 以table id查deskcode，deskcode查局号
    	JSONObject deskJson = deskBusiness.queryDeskById(betVO.getTableId());
    	if ( deskJson==null ) {
    		log.warn("[下注] 桌台号查无资料, table id: {}", betVO.getTableId());
            return ResponseUtil.custom("桌台资料错误");
    	}
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskJson.getString("deskCode"));
    	
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.warn("[下注] user查无资料, id: {}", userIdOrName);
            return ResponseUtil.custom("玩家资料错误");
    	}
    	
    	// 检核限红
    	GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(betVO.getTgChatId());
    	if ( groupInfo.checkTotalBetAmount(betVO.getAmount(), betVO.getMaxShoeAmount().longValue())==false ) {
            return ResponseUtil.custom(String.format("下注失败 达到当局最大投注 %s", betVO.getMaxShoeAmount()));
    	}
    	
    	if ( groupInfo.checkUserBetAmount(userVO.getId(), betVO.getAmount()
    			, betVO.getMinAmount().longValue(), betVO.getMaxAmount().longValue())==false ) {
            return ResponseUtil.custom(String.format("下注失败 限红单注 %s-%s", betVO.getMinAmount(), betVO.getMaxAmount()));
    	}

    	//  呼叫
    	//	会员管理-下分api
    	String withdrawResult = assetsBusiness.withdraw(userVO.getId(), betVO.getAmount());
    	if ( StringUtils.isNotBlank(withdrawResult) ) {
    		log.warn("[下注] 下分失败");
            return ResponseUtil.custom(withdrawResult);
    	}
    	
		// 记录IP
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		//	TODO 輪/局號 應來自荷官端，不得從請求中代入
    	String betResult = orderBusiness.bet(isTgRequest, betVO.getTableId(), betVO.getTgChatId()
    			, ip, userVO.getId(), userVO.getUserName(), betVO.getBetOption(), betVO.getAmount(), "00001");
    	if ( StringUtils.isNotBlank(betResult) ) {
            return ResponseUtil.custom(betResult);
    	}

		log.info("[下注] 成功");
        return ResponseUtil.success();
    }


    @PostMapping("currentList")
    @ApiOperation(("近期订单"))
    public ResponseEntity<List<BetResponseVO>> currentList() {
		log.info("近期订单");
		JSONObject result = orderBusiness.currentOrderList();
        if ( result==null ) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.success(result);
    }
}
