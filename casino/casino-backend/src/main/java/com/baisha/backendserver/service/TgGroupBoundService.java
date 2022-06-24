package com.baisha.backendserver.service;

import com.baisha.backendserver.model.TgGroupBound;
import com.baisha.backendserver.repository.TgGroupBoundRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yihui
 */
@Slf4j
@Service
public class TgGroupBoundService {


    @Autowired
    private TgGroupBoundRepository tgGroupBoundRepository;

    public TgGroupBound save(TgGroupBound tgGroupBound) {
        tgGroupBoundRepository.save(tgGroupBound);
        return tgGroupBound;
    }


    public TgGroupBound findByTgGroupId(String tgGroupId) {
        return tgGroupBoundRepository.findByTgGroupId(tgGroupId);
    }


}
