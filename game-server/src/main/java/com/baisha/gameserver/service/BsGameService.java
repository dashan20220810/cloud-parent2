package com.baisha.gameserver.service;

import com.baisha.gameserver.model.BsGame;
import com.baisha.gameserver.repository.BsGameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class BsGameService {

    @Autowired
    private BsGameRepository gameRepository;

    public List<BsGame> findAll() {
        List<BsGame> list = gameRepository.findAll();
        return list;
    }

    public BsGame save(BsGame bsGame) {
        gameRepository.save(bsGame);
        return bsGame;
    }
}
