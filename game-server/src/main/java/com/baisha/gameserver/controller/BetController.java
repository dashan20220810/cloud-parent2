package com.baisha.gameserver.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.model.BetStatistics;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.vo.BetPageVO;
import com.baisha.gameserver.vo.BetVO;
import com.baisha.gameserver.vo.response.BetResponseVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.util.PageUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@RestController
@Api(tags = "注单管理")
@RequestMapping("order")
@Slf4j
public class BetController {

	
    @Value("${project.game.return-amount-multiplier}")
    private BigDecimal gameReturnAmountMultiplier;

    @Autowired
    BetService betService;

    @Autowired
    BetStatisticsService betStatisticsService;

    @PostMapping("bet")
    @ApiOperation("下注")
    public ResponseEntity<String> bet(BetVO betVO) {

        log.info("[下注] ");
        Bet bet = betVO.generateBet();

        if (Bet.checkRequestForGs(bet, betVO.isTgRequest()) == false) {
            log.info("[下注] 检核失败");
            return ResponseUtil.custom("命令错误，请参考下注规则");
        }

        Bet newBet = betService.save(bet);

        String dateStr = DateUtil.today(DateUtil.YYYYMMDD);
        Long userId = betVO.getUserId();
        BigDecimal flowAmount = new BigDecimal(bet.getFlowAmount());

        BetStatistics betStatistics = betStatisticsService.findByUserIdAndTgChatIdAndStatisticsDate(userId, betVO.getTgChatId(), Integer.parseInt(dateStr));

        if (betStatistics == null) {
            betStatistics = new BetStatistics();
            betStatistics.setTgChatId(betVO.getTgChatId());
            betStatistics.setUserId(userId);
            betStatistics.setStatisticsDate(Integer.parseInt(dateStr));
            betStatistics.setFlowAmount(flowAmount);
            betStatisticsService.save(betStatistics);
        } else {
            betStatisticsService.updateFlowAmount(userId, betVO.getTgChatId(), Integer.parseInt(dateStr), flowAmount);
        }

//		log.info("[下注] 成功! 押{} 共{}", betVO.getBetOption().getDisplay(), betVO.getAmount());
        log.info("[下注] 成功! 庄{} 闲{} 和{} 庄对{} 闲对{} 超级六{}", betVO.getAmountZ(), betVO.getAmountX(), betVO.getAmountH()
                , betVO.getAmountZd(), betVO.getAmountXd(), betVO.getAmountSs());
        return ResponseUtil.success(newBet.getId().toString());
    }

    @PostMapping("page")
    @ApiOperation("订单查询")
    public ResponseEntity<Page<Bet>> page(BetPageVO vo) {
        log.info("订单查询");
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<Bet> pageList = betService.getBetPage(vo, pageable);
        return ResponseUtil.success(pageList);
    }

    @PostMapping("settlement")
    @ApiOperation("结算订单")
    public ResponseEntity<List<Bet>> settlement(String noActive) {
        Integer settlementStatus = 2;

        log.info("结算订单");
        return ResponseUtil.success(betService.findBetNoSettle(noActive, settlementStatus));
    }

    @PostMapping("currentList")
    @ApiOperation("近期订单")
    public ResponseEntity<List<Bet>> currentList(Long userId, Long tgChatId, Integer queryAmount) {

        if ( userId==null || tgChatId==null ) {
            log.info("[近期订单] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        if ( queryAmount==null ) {
        	queryAmount = 10;
        } else if ( queryAmount > 100 ) {
        	queryAmount = 100;
        }
        log.info("近期订单");
        return ResponseUtil.success(betService.findAllByUserIdAndTgChatId(userId, tgChatId, queryAmount));
    }

    @PostMapping("delete")
    @ApiOperation("删除订单")
    public ResponseEntity<String> delete(Long betId) {

        if ( betId==null ) {
            log.info("[删除订单] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        log.info("删除订单 id: {}", betId);
        betService.delete(betId);
        return ResponseUtil.success();
    }

    @GetMapping("todayTotalWater")
    @ApiOperation("当日流水")
    public ResponseEntity<String> todayTotalWater(Long userId, Long tgChatId) {

        if ( userId==null || tgChatId==null ) {
            log.info("[当日流水] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        log.info("当日流水 user id: {}, tgChatId: {}", userId, tgChatId);
        BetStatistics entity = betStatisticsService.findByUserIdAndTgChatIdAndStatisticsDate(userId, tgChatId);
        return ResponseUtil.success(entity==null||entity.getFlowAmount()==null ? "0.00" : entity.getFlowAmount().toString());
    }

    @GetMapping("todayTotalProfit")
    @ApiOperation("当日盈利")
    public ResponseEntity<String> todayTotalProfit(Long userId, Long tgChatId) {

        if ( userId==null || tgChatId==null ) {
            log.info("[当日盈利] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        log.info("当日盈利 user id: {}, tgChatId: {}", userId, tgChatId);
        BetStatistics entity = betStatisticsService.findByUserIdAndTgChatIdAndStatisticsDate(userId, tgChatId);
        return ResponseUtil.success(entity==null||entity.getWinAmount()==null ? "0.00" : entity.getWinAmount().toString());
    }

    @GetMapping("queryBetIsNotReturned")
    @ApiOperation("查询未返水")
    public ResponseEntity<List<BetResponseVO>> queryBetIsNotReturned(Long userId, Long tgChatId) {

        if ( userId==null || tgChatId==null ) {
            log.info("[查询未返水] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        log.info("查询未返水 user id: {}, tgChatId: {}", userId, tgChatId);
        List<Bet> betList = betService.queryBetIsNotReturned(userId, tgChatId);
        
        if (betList!=null) {
            return ResponseUtil.success(betList.stream().map(bet -> {
            	BetResponseVO vo = new BetResponseVO();
            	BeanUtils.copyProperties(bet, vo);
            	vo.setId(bet.getId());
            	return vo;
            }).collect(Collectors.toList()));
        }
        
        return ResponseUtil.success(new ArrayList<>());
    }
    
    @PostMapping("returnAmount")
    @ApiOperation("返水")
    public ResponseEntity<BigDecimal> returnAmount(Long betId, Long userId, Long tgChatId) {

        if ( betId==null || userId==null || tgChatId==null ) {
            log.info("[返水] 检核失败");
            return ResponseUtil.custom("检核失败");
        }
        log.info("返水 betId: {}, userId: {}, tgChatId: {}", betId, userId, tgChatId);
        BigDecimal returnAmount = BigDecimal.ZERO;
        Bet bet = betService.findById(betId);
        returnAmount = gameReturnAmountMultiplier.multiply(bet.getWinAmount()).abs();
        betStatisticsService.updateReturnAmount(userId, tgChatId, Integer.parseInt(DateUtil.today(DateUtil.YYYYMMDD)), returnAmount);
        betService.updateReturnAmount(betId, returnAmount);
        return ResponseUtil.success(returnAmount);
    }
    
    
}