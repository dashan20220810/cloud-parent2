package com.baisha.gameserver.controller;

import java.math.BigDecimal;
import java.util.List;

import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

        if (Bet.checkRequestForGs(bet, betVO.isTgRequest()) == false) {
            log.info("[下注] 检核失败");
            return ResponseUtil.custom("命令错误，请参考下注规则");
        }

        betService.save(bet);

//		log.info("[下注] 成功! 押{} 共{}", betVO.getBetOption().getDisplay(), betVO.getAmount());
        log.info("[下注] 成功! 庄{} 闲{} 和{} 庄对{} 闲对{} 超级六{}", betVO.getAmountZ(), betVO.getAmountX(), betVO.getAmountH()
                , betVO.getAmountZd(), betVO.getAmountXd(), betVO.getAmountSs());
        return ResponseUtil.success();
    }

    @PostMapping("page")
    @ApiOperation("订单查询")
    public ResponseEntity<Page<Bet>> page(BetPageVO vo) {

        log.info("订单查询");
        Page<Bet> pageList = betService.getBetPage(vo);
        return ResponseUtil.success(pageList);
    }

    @PostMapping("currentList")
    @ApiOperation("近期订单")
    public ResponseEntity<List<Bet>> currentList(Long userId) {

        log.info("近期订单");
        return ResponseUtil.success(betService.findAllByUserId(userId));
    }


  /*  @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostMapping("tt")
    @ApiOperation("tt")
    public ResponseEntity tt() {
        rabbitTemplate.convertAndSend(MqConstants.BET_SETTLEMENT, BetSettleVO.builder().noActive("G01202206301004").awardOption("X").build());
        return ResponseUtil.success();
    }
*/

}