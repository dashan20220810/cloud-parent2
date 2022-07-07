package com.baisha.gameserver.service;

import com.baisha.gameserver.model.BsOdds;
import com.baisha.gameserver.repository.BsOddsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class BsOddsService {

    @Autowired
    private BsOddsRepository oddsRepository;

    public List<BsOdds> findAllByGameCode(String gameCode) {
        return oddsRepository.findAllByGameCode(gameCode);
    }
}
