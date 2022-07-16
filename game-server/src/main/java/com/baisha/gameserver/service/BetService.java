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
import com.baisha.modulecommon.util.DateUtil;

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
    
    public Bet findById(Long id) {
    	return betRepository.findById(id).get();
    }

    public Page<Bet> getBetPage(BetPageVO vo, Pageable pageable) {
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
        Page<Bet> page = betRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
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
        Date yesterdayStartTime = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), -1);
        Date yesterdayEndTime = DateUtils.addDays(yesterdayStartTime, 1);
        yesterdayEndTime = DateUtils.addMilliseconds(yesterdayEndTime, -1);
        return betRepository.findAllBy(yesterdayStartTime, yesterdayEndTime, PageRequest.of(0, queryAmount));
    }

    public void updateReturnAmount(Long betId, BigDecimal returnAmount) {
        betRepository.updateIsReturnedAndReturnAmountById(betId, returnAmount);
    }

    public List<Bet> findByNoActive(String noActive) {
        return betRepository.findByNoActive(noActive);
    }
}