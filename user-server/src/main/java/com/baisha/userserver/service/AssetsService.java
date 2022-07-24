package com.baisha.userserver.service;

import com.baisha.userserver.model.Assets;
import com.baisha.userserver.repository.AssetsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional
public class AssetsService {

    @Autowired
    private AssetsRepository assetsRepository;
    @Autowired
    EntityManager entityManager;


    public void doRefresh(Assets assets) {
        entityManager.refresh(assets);
    }



    public Assets saveAssets(Assets assets) {
        assetsRepository.save(assets);
        return assets;
    }

    /*public void doFlushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }*/

    public Assets getAssetsByUserId(Long id) {
        return assetsRepository.findByUserId(id);
    }


    public int doIncreaseBalanceById(BigDecimal amount, Long id) {
        if (Objects.isNull(amount)) {
            log.error("doIncreaseBalanceById amount is null");
            amount = BigDecimal.ZERO;
        }
        return assetsRepository.increaseBalanceById(amount, id);
    }

    public int doReduceBalanceById(BigDecimal amount, Long id) {
        if (Objects.isNull(amount)) {
            log.error("doReduceBalanceById amount is null");
            amount = BigDecimal.ZERO;
        }
        return assetsRepository.reduceBalanceById(amount, id);
    }

    public int doIncreasePlayMoneyById(BigDecimal amount, Long id) {
        if (Objects.isNull(amount)) {
            log.error("doIncreasePlayMoneyById amount is null");
            amount = BigDecimal.ZERO;
        }
        return assetsRepository.increasePlayMoneyById(amount, id);
    }

    public int doReducePlayMoneyById(BigDecimal amount, Long id) {
        if (Objects.isNull(amount)) {
            log.error("doReducePlayMoneyById amount is null");
            amount = BigDecimal.ZERO;
        }
        return assetsRepository.reducePlayMoneyById(amount, id);
    }

    public int doSubtractBalanceById(BigDecimal amount, Long id) {
        if (Objects.isNull(amount)) {
            log.error("doSubtractBalanceById amount is null");
            amount = BigDecimal.ZERO;
        }
        return assetsRepository.doSubtractBalanceById(amount, id);
    }

}
