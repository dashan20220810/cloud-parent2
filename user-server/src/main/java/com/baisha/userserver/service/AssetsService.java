package com.baisha.userserver.service;

import com.baisha.userserver.model.Assets;
import com.baisha.userserver.repository.AssetsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional
public class AssetsService {

    @Autowired
    private AssetsRepository assetsRepository;

    public Assets saveAssets(Assets assets) {
        assetsRepository.save(assets);
        return assets;
    }

    public Assets getAssetsByUserId(Long id) {
        return assetsRepository.findByUserId(id);
    }


    public int doIncreaseBalanceById(BigDecimal amount, Long userId) {
        return assetsRepository.increaseBalanceById(amount, userId);
    }

    public int doReduceBalanceById(BigDecimal amount, Long userId) {
        return assetsRepository.reduceBalanceById(amount, userId);
    }
}
