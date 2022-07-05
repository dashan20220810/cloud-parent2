package com.baisha.gameserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
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
import com.baisha.gameserver.vo.BetPageVO;
import com.baisha.modulecommon.util.PageUtil;

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
    
    public void delete( Long id ) {
    	betRepository.deleteById(id);
    }

    public Page<Bet> getBetPage(BetPageVO vo) {
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

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
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
    public List<Bet> findAllByUserId(Long userId) {
        return betRepository.findAllByUserId(userId, PageRequest.of(0, 20, Sort.by("createTime").descending()));
    }

    public List<Bet> findBetNoSettle(String noActive, Integer status) {
        return betRepository.findByNoActiveAndStatus(noActive, status);
    }

    public int settleBet(Long id, BigDecimal winAmount, BigDecimal finalAmount) {
        return betRepository.updateSettleBetById(id, winAmount, finalAmount, new Date());
    }
    
    public BigDecimal todayTotalProfit( Long userId ) {
    	Date todayStartTime = DateUtils.truncate(new Date(), Calendar.DATE);
    	Date todayEndTime = DateUtils.addDays(todayStartTime, 1);
    	todayEndTime = DateUtils.addMilliseconds(todayEndTime, -1);
    	BigDecimal result = betRepository.todayTotalProfit(userId, todayStartTime, todayEndTime);
    	return result==null ? BigDecimal.ZERO : result;
    }
    
    public BigDecimal todayTotalWater( Long userId ) {
    	Date todayStartTime = DateUtils.truncate(new Date(), Calendar.DATE);
    	Date todayEndTime = DateUtils.addDays(todayStartTime, 1);
    	todayEndTime = DateUtils.addMilliseconds(todayEndTime, -1);
    	BigDecimal result = betRepository.todayTotalWater(userId, todayStartTime, todayEndTime);
    	return result==null ? BigDecimal.ZERO : result;
    }
}