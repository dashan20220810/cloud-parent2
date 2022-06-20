package com.baisha.userserver.repository;

import com.baisha.userserver.model.Assets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

/**
 * @author yihui
 */
public interface AssetsRepository extends JpaRepository<Assets, Long>, JpaSpecificationExecutor<Assets> {
    /**
     * 根据用户ID查询
     *
     * @param id
     * @return
     */
    Assets findByUserId(Long id);

    /**
     * 增加余额
     *
     * @param amount
     * @param userId
     * @return
     */
    @Query(value = "update Assets a set a.balance = a.balance + ?1 where a.userId=?2 ")
    @Modifying
    int increaseBalanceByUserId(BigDecimal amount, Long userId);

    /**
     * 减少余额
     *
     * @param amount
     * @param userId
     * @return
     */
    @Query(value = "update Assets a set a.balance = a.balance - ?1 where a.userId=?2  and a.balance >= ?1 ")
    @Modifying
    int reduceBalanceByUserId(BigDecimal amount, Long userId);


}
