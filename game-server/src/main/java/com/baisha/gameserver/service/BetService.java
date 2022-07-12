package com.baisha.gameserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.repository.BetRepository;

/**
 * @author: alvin
 */
@Service
@Transactional
public class BetService {


    @Autowired
    BetRepository betRepository;

    public Bet save(Bet bet) {
        return betRepository.save(bet);
    }

    public void delete(Long id) {
        betRepository.deleteById(id);
    }

    public Page<Bet> getBetPage(Specification<Bet> spec, Pageable pageable) {
        Page<Bet> page = betRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    public List<Bet> findAllByUserIdAndCreateTimeBetween(Long userId, Date createBeginTime, Date createEndTime) {
        return betRepository.findAllByUserIdAndCreateTimeBetween(userId, createBeginTime, createEndTime);
    }

    /**
     * 近期20笔
     *
     * @param userId
     * @return
     */
    public List<Bet> findAllByUserIdAndTgChatId(Long userId, Long tgChatId, Integer queryAmount) {
        return betRepository.findAllByUserIdAndTgChatId(userId, tgChatId, PageRequest.of(0, queryAmount, Sort.by("createTime").descending()));
    }

    public List<Bet> findBetNoSettle(String noActive, Integer status) {
        return betRepository.findByNoActiveAndStatus(noActive, status);
    }

    public int settleBet(Long id, BigDecimal winAmount, BigDecimal finalAmount, String settleRemark) {
        return betRepository.updateSettleBetById(id, winAmount, finalAmount, new Date(), settleRemark);
    }

    /**
     * status=2 AND (b.isReturned IS NULL or b.isReturned = false) AND b.winAmount < 0 AND updateTime BETWEEN today
     *
     * @param userId
     * @param tgChatId
     * @return
     */
    public List<Bet> queryBetIsNotReturned(Long userId, Long tgChatId) {

        Date todayStartTime = DateUtils.truncate(new Date(), Calendar.DATE);
        Date todayEndTime = DateUtils.addDays(todayStartTime, 1);
        todayEndTime = DateUtils.addMilliseconds(todayEndTime, -1);
        return betRepository.findAllBy(userId, tgChatId, todayStartTime, todayEndTime);
    }

    /**
     * status=2 AND (b.isReturned IS NULL or b.isReturned = false) AND b.winAmount < 0 AND updateTime BETWEEN today
     *
     * @return
     */
    public List<Bet> queryBetIsNotReturnedYesterday(Integer queryAmount) {

        Date yesterdayStartTime = DateUtils.truncate(new Date(), Calendar.DATE);
        Date yesterdayEndTime = DateUtils.addDays(yesterdayStartTime, 1);
        yesterdayEndTime = DateUtils.addMilliseconds(yesterdayEndTime, -1);
        return betRepository.findAllBy(yesterdayStartTime, yesterdayEndTime, PageRequest.of(0, queryAmount));
    }

    public void updateReturnAmount(Long betId) {
        betRepository.updateIsReturnedById(betId);
    }

}