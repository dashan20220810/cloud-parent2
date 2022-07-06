package com.baisha.gameserver.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
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

import javax.persistence.criteria.Predicate;

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

        Bet newBet = betService.save(bet);

//		log.info("[下注] 成功! 押{} 共{}", betVO.getBetOption().getDisplay(), betVO.getAmount());
        log.info("[下注] 成功! 庄{} 闲{} 和{} 庄对{} 闲对{} 超级六{}", betVO.getAmountZ(), betVO.getAmountX(), betVO.getAmountH()
                , betVO.getAmountZd(), betVO.getAmountXd(), betVO.getAmountSs());
        return ResponseUtil.success(newBet.getId());
    }

    @PostMapping("page")
    @ApiOperation("订单查询")
    public ResponseEntity<Page<Bet>> page(BetPageVO vo) {
        log.info("订单查询");
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Specification<Bet> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StringUtils.isNotBlank(vo.getUserName())) {
                predicates.add(cb.or(
                        cb.like(root.get("userName"), "%" + vo.getUserName().trim() + "%"),
                        cb.like(root.get("nickName"), "%" + vo.getUserName().trim() + "%"))
                );
            }
            if (StringUtils.isNotBlank(vo.getNoActive())) {
                predicates.add(cb.like(root.get("noActive"), "%" + vo.getNoActive() + "%"));
            }
            if (vo.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), vo.getStatus()));
            }
            try {
                if (StringUtils.isNotEmpty(vo.getStartTime())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.getSimpleDateFormat().parse(vo.getStartTime().trim())));
                }
                if (StringUtils.isNotEmpty(vo.getEndTime())) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.getSimpleDateFormat().parse(vo.getEndTime().trim())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<Bet> pageList = betService.getBetPage(spec, pageable);
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
    public ResponseEntity<List<Bet>> currentList(Long userId) {

        log.info("近期订单");
        return ResponseUtil.success(betService.findAllByUserId(userId));
    }

    @PostMapping("delete")
    @ApiOperation("删除订单")
    public ResponseEntity<String> delete(Long betId) {

        log.info("删除订单 id: {}", betId);
        betService.delete(betId);
        return ResponseUtil.success();
    }

    @GetMapping("todayTotalWater")
    @ApiOperation("当日流水")
    public ResponseEntity<String> todayTotalWater(Long userId) {

        log.info("当日流水 user id: {}", userId);
        return ResponseUtil.success(betService.todayTotalWater(userId));
    }

    @GetMapping("todayTotalProfit")
    @ApiOperation("当日盈利")
    public ResponseEntity<String> todayTotalProfit(Long userId) {

        log.info("当日盈利 user id: {}", userId);
        return ResponseUtil.success(betService.todayTotalProfit(userId));
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