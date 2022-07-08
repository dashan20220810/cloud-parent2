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
import com.baisha.gameserver.vo.BetReturnAmountVO;

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

    public int settleBet(Long id, BigDecimal winAmount, BigDecimal finalAmount) {
        return betRepository.updateSettleBetById(id, winAmount, finalAmount, new Date());
    }

    public BigDecimal returnAmount ( Long userId, Long tgChatId, BigDecimal gameReturnAmountMultiplier ) {

        Date todayStartTime = DateUtils.truncate(new Date(), Calendar.DATE);
        Date todayEndTime = DateUtils.addDays(todayStartTime, 1);
        todayEndTime = DateUtils.addMilliseconds(todayEndTime, -1);
    	BigDecimal totalFlowAmount = betRepository.sumFlowAmount(userId, tgChatId, todayStartTime, todayEndTime);
    	if ( totalFlowAmount==null || totalFlowAmount.equals(BigDecimal.ZERO) ) {
    		return BigDecimal.ZERO;
    	}
    	
    	betRepository.updateReturnAmount(userId, tgChatId, todayStartTime, todayEndTime, true);
    	return totalFlowAmount.multiply(gameReturnAmountMultiplier);
    }
    
    public List<BetReturnAmountVO> returnAmountByDay ( BigDecimal gameReturnAmountMultiplier ) {

        Date yesterdayStartTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -1);
        Date yesterdayEndTime = DateUtils.addMilliseconds(DateUtils.addDays(yesterdayStartTime, 1), -1);
    	
        List<BetReturnAmountVO> result = betRepository.sumFlowAmount(yesterdayStartTime, yesterdayEndTime);
        
        result.forEach( vo -> {
        	betRepository.updateReturnAmount(vo.getUserId(), vo.getTgChatId(), yesterdayStartTime, yesterdayEndTime, true);
        	if ( vo.getTotalFlowAmount()!=null && !vo.getTotalFlowAmount().equals(BigDecimal.ZERO) ) {
        		vo.setTotalReturnAmount( gameReturnAmountMultiplier.multiply(new BigDecimal(vo.getTotalFlowAmount())) );
        	}
        });
    	return result;
    }
}