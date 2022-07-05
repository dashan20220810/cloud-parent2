package com.baisha.userserver.service;

import com.baisha.userserver.model.PlayMoneyChange;
import com.baisha.userserver.repository.PlayMoneyChangeRepository;
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
@Transactional(rollbackFor = Exception.class)
public class PlayMoneyChangeService {

    @Autowired
    private PlayMoneyChangeRepository playMoneyChangeRepository;

    public PlayMoneyChange save(PlayMoneyChange change) {
        playMoneyChangeRepository.save(change);
        return change;
    }

    public Page<PlayMoneyChange> getUserPlayMoneyChangePage(Specification<PlayMoneyChange> spec, Pageable pageable) {
        Page<PlayMoneyChange> pageList = playMoneyChangeRepository.findAll(spec, pageable);
        return Optional.ofNullable(pageList).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }
}
