package com.baisha.userserver.service;

import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.repository.BalanceChangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
