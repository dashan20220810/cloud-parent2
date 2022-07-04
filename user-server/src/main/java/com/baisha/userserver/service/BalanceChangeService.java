package com.baisha.userserver.service;

import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.User;
import com.baisha.userserver.repository.BalanceChangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class BalanceChangeService {

    @Autowired
    private BalanceChangeRepository balanceChangeRepository;

    public BalanceChange save(BalanceChange balanceChange) {
        balanceChangeRepository.save(balanceChange);
        return balanceChange;
    }

    public Page<BalanceChange> getUserBalanceChangePage(Specification<BalanceChange> spec, Pageable pageable) {
        Page<BalanceChange> pageList = balanceChangeRepository.findAll(spec, pageable);
        return Optional.ofNullable(pageList).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }
}
