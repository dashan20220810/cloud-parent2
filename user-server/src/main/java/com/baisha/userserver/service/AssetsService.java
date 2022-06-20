package com.baisha.userserver.service;

import com.baisha.userserver.model.Assets;
import com.baisha.userserver.repository.AssetsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "assets")
public class AssetsService {

    @Autowired
    private AssetsRepository assetsRepository;

    @Cacheable(key = "#p0.userId")
    public Assets saveAssets(Assets assets) {
        assetsRepository.save(assets);
        return assets;
    }

    public Assets getAssetsByUserIdSql(Long id) {
        return assetsRepository.findByUserId(id);
    }


    public int doIncreaseBalanceByUserId(BigDecimal amount, Long userId) {
        return assetsRepository.increaseBalanceByUserId(amount, userId);
    }

    public int doReduceBalanceByUserId(BigDecimal amount, Long userId) {
        return assetsRepository.reduceBalanceByUserId(amount, userId);
    }
}
