package com.baisha.casinoweb.controller;

import java.math.BigDecimal;
import java.util.List;

import com.baisha.modulecommon.vo.mq.webServer.BsOddsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.business.OrderBusiness;
import com.baisha.casinoweb.model.vo.BetVO;
import com.baisha.casinoweb.model.vo.response.BetResponseVO;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

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
	
//	@Autowired
//	private UserBusiness userBusiness;
	
//	@Autowired
//	private AssetsBusiness assetsBusiness;
	
	@Autowired
	private OrderBusiness orderBusiness;
	
//	@Autowired
//	private DeskBusiness deskBusiness;
//	
//	@Autowired 
//	private GameInfoBusiness gameInfoBusiness;

    @PostMapping("bet")
    @ApiOperation("下注")
    public ResponseEntity<String> bet(BetVO betVO) {

		log.info("[下注]");
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	
    	if ( !BetVO.checkRequest(betVO)
    			|| (isTgRequest && betVO.getTgChatId()==null) ) {
    		log.info("[下注] 检核失败");
            return ResponseUtil.custom("命令错误，请参考下注规则");
    	}
    	
//    	if ( betVO.getMinAmount()==null || betVO.getMaxAmount()==null || betVO.getMaxShoeAmount()==null ) {
//    	if ( betVO.getMinAmount()==null || betVO.getMaxAmount()==null ) {
//    		log.info("[下注] 检核失败");
//            return ResponseUtil.custom("命令错误，请参考下注规则");
//    	}

		//	TODO 輪/局號 應來自荷官端，不得從請求中代入
    	String betResult = orderBusiness.bet(isTgRequest, betVO, "00001");
    	if ( StringUtils.isNotBlank(betResult) ) {
            return ResponseUtil.custom(betResult);
    	}

		log.info("[下注] 成功");
        return ResponseUtil.success();
    }


    @PostMapping("currentList")
    @ApiOperation(("近期订单"))
    public ResponseEntity<List<BetResponseVO>> currentList(Long tgChatId, Integer queryAmount) {
		log.info("近期订单");
        return ResponseUtil.success(orderBusiness.currentOrderList(tgChatId, queryAmount));
    }

    @GetMapping("todayTotalWater")
    @ApiOperation("当日流水")
    public ResponseEntity<BigDecimal> todayTotalWater(Long tgChatId) {

        log.info("当日流水");
        BigDecimal result = orderBusiness.todayTotalWater(tgChatId);
        if ( result==null ) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.success(result);
    }

    @GetMapping("todayTotalProfit")
    @ApiOperation("当日盈利")
    public ResponseEntity<BigDecimal> todayTotalProfit(Long tgChatId) {

        log.info("当日盈利");
        BigDecimal result = orderBusiness.todayTotalProfit(tgChatId);
        if ( result==null ) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.success(result);
    }

    @GetMapping("returnAmount")
    @ApiOperation("返水")
    public ResponseEntity<BigDecimal> returnAmount(Long tgChatId) {

        if ( tgChatId==null ) {
            log.info("[返水] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        log.info("返水 tgChatId: {}", tgChatId);
        BigDecimal returnAmount = orderBusiness.returnAmount(tgChatId);
        return ResponseUtil.success(ResponseUtil.formatOutput(returnAmount));
    }


    @GetMapping("redLimit")
    @ApiOperation("查询限红")
    public ResponseEntity<List<BsOddsVO>> redLimit() {

        log.info("查询限红");
        List<BsOddsVO> bsOdds = orderBusiness.redLimit();
        return ResponseUtil.success(bsOdds);
    }
}
