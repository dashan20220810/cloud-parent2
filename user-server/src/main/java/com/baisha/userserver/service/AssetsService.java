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


    public int doIncreaseBalanceById(BigDecimal amount, Long id) {
        return assetsRepository.increaseBalanceById(amount, id);
    }

    public int doReduceBalanceById(BigDecimal amount, Long id) {
        return assetsRepository.reduceBalanceById(amount, id);
    }

    public int doIncreasePlayMoneyById(BigDecimal amount, Long id) {
        return assetsRepository.increasePlayMoneyById(amount, id);
    }

    public int doReducePlayMoneyById(BigDecimal amount, Long id) {
        return assetsRepository.reducePlayMoneyById(amount, id);
    }
}
