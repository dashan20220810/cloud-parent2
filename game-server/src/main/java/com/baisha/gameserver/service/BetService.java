package com.baisha.gameserver.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

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
    public List<Bet> findAllByUserId(Long userId) {
        return betRepository.findAllByUserId(userId, PageRequest.of(0, 20, Sort.by("createTime").descending()));
    }

    public List<Bet> findBetNoSettle(String noActive, Integer status) {
        return betRepository.findByNoActiveAndStatus(noActive, status);
    }

    public int settleBet(Long id, BigDecimal winAmount, BigDecimal finalAmount) {
        return betRepository.updateSettleBetById(id, winAmount, finalAmount, new Date());
    }

}