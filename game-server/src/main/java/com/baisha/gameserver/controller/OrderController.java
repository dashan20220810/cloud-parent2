package com.baisha.gameserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.vo.BetPageVO;
import com.baisha.gameserver.vo.BetVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@RestController
@Api(tags = "订单管理")
@RequestMapping("order")
@Slf4j
public class OrderController {

	@Autowired
	BetService betService;

    @PostMapping("bet")
    @ApiOperation("下注")
    public ResponseEntity<String> bet(BetVO betVO) {

		log.info("[下注] ");
    	Bet bet = betVO.generateBet();
    	
    	if ( Bet.checkRequestForGs(bet, betVO.isTgRequest())==false ) {
    		log.info("[下注] 检核失败");
    		return ResponseUtil.fail();
    	}
    	
    	betService.save(bet);

		log.info("[下注] 成功! 押{} 共{}", betVO.getBetOption().getDisplay(), betVO.getAmount());
    	return ResponseUtil.success();
    }

    @PostMapping("page")
    @ApiOperation("订单查询")
    public ResponseEntity<Page<Bet>> page(BetPageVO vo) {
    	
    	log.info("订单查询");
        Page<Bet> pageList = betService.getBetPage(vo);
        return ResponseUtil.success(pageList);
    }
    
}