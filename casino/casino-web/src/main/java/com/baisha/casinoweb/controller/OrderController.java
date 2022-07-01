package com.baisha.casinoweb.controller;

import java.util.Date;
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
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.GameTgGroupInfo;
import com.baisha.modulecommon.vo.GameUserInfo;

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
    	Date now = new Date();
    	
    	if ( BetVO.checkRequest(betVO)==false 
    			|| (isTgRequest && betVO.getTgChatId()==null) ) {
    		log.info("[下注] 检核失败");
            return ResponseUtil.custom("命令错误，请参考下注规则");
    	}
    	
    	if ( betVO.getMinAmount()==null || betVO.getMaxAmount()==null || betVO.getMaxShoeAmount()==null ) {
    		log.info("[下注] 检核失败");
            return ResponseUtil.custom("命令错误，请参考下注规则");
    	}
    	
    	// 桌台资料
    	JSONObject deskJson = deskBusiness.queryDeskById(betVO.getTableId());
    	if ( deskJson==null ) {
    		log.warn("[下注] 桌台号查无资料, table id: {}", betVO.getTableId());
            return ResponseUtil.custom("桌台资料错误");
    	}
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskJson.getString("deskCode"));
		
		if ( gameInfo.getStatus()!=GameStatusEnum.Betting || (gameInfo.getEndTime()!=null && now.after(gameInfo.getEndTime())) ) {
    		log.warn("下注 失败 非下注状态, {}", gameInfo.getStatus().toString());
            return ResponseUtil.custom("下注 失败 非下注状态");
		}
    	
    	// 检核限红
    	GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(betVO.getTgChatId());
    	if ( groupInfo.checkTotalBetAmount(betVO.getTotalAmount(), betVO.getMaxShoeAmount().longValue())==false ) {
            return ResponseUtil.custom(String.format("下注失败 达到当局最大投注 %s", betVO.getMaxShoeAmount()));
    	}
    	
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.warn("[下注] user查无资料, id: {}", userIdOrName);
            return ResponseUtil.custom("玩家资料错误");
    	}
    	
    	GameUserInfo userInfo = groupInfo.getUserInfo(userVO.getId());
    	if ( userInfo.checkUserBetAmount(betVO.getTotalAmount()
    			, betVO.getMinAmount().longValue(), betVO.getMaxAmount().longValue())==false ) {
            return ResponseUtil.custom(String.format("下注失败 限红单注 %s-%s", betVO.getMinAmount(), betVO.getMaxAmount()));
    	}

    	//  呼叫
    	//	会员管理 - 下分api
    	String withdrawResult = assetsBusiness.withdraw(userVO.getId(), betVO.getTotalAmount(), betVO.getTableId());
    	if ( StringUtils.isNotBlank(withdrawResult) ) {
    		log.warn("[下注] 下分失败");
            return ResponseUtil.custom(withdrawResult);
    	}
    	
		//	TODO 輪/局號 應來自荷官端，不得從請求中代入
    	String betResult = orderBusiness.bet(isTgRequest, betVO, userVO, "00001");
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
