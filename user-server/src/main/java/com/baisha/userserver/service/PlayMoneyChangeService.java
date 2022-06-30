package com.baisha.userserver.service;

import com.baisha.userserver.model.PlayMoneyChange;
import com.baisha.userserver.repository.PlayMoneyChangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PlayMoneyChangeService {

    @Autowired
    private PlayMoneyChangeRepository playMoneyChangeRepository;

    public PlayMoneyChange save(PlayMoneyChange change) {
        playMoneyChangeRepository.save(change);
        return change;
    }
}
