package com.baisha.gameserver.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baisha.gameserver.model.BetResult;
import com.baisha.gameserver.repository.BetResultRepository;

/**
 * @author: alvin
 */
@Service
@Transactional
public class BetResultService {


    @Autowired
    BetResultRepository betResultRepository;

    public void save(BetResult betResult) {
        betResultRepository.save(betResult);
    }
    
    public void update ( Long tableId, String noActive, String awardOption ) {
    	betResultRepository.UpdateAwardOptionByTableIdAndNoActive(awardOption, tableId, noActive);
    }
}
