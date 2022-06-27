package com.baisha.gameserver.service;

import com.baisha.gameserver.model.TgDesk;
import com.baisha.gameserver.repository.TgDeskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yihui
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "tgDesk::id")
@Transactional(rollbackFor = Exception.class)
public class TgDeskService {

    @Autowired
    private TgDeskRepository tgDeskRepository;

    public List<TgDesk> findAllDeskList() {
        return tgDeskRepository.findAllDeskList();
    }


}
