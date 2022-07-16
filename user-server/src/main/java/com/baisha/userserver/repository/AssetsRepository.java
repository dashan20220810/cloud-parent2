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
     * @param id
     * @return
     */
    @Query(value = "update Assets a set a.balance = a.balance + ?1 where a.id=?2 ")
    @Modifying
    int increaseBalanceById(BigDecimal amount, Long id);

    /**
     * 减少余额
     *
     * @param amount
     * @param id
     * @return
     */
    @Query(value = "update Assets a set a.balance = a.balance - ?1 where a.id=?2  and a.balance >= ?1 ")
    @Modifying
    int reduceBalanceById(BigDecimal amount, Long id);

    /**
     * 增加打码量
     *
     * @param amount
     * @param id
     * @return
     */
    @Query(value = "update Assets a set a.playMoney = a.playMoney + ?1 where a.id=?2 ")
    @Modifying
    int increasePlayMoneyById(BigDecimal amount, Long id);

    /**
     * 减少打码量
     *
     * @param amount
     * @param id
     * @return
     */
    @Query(value = "update Assets a set a.playMoney = a.playMoney - ?1 where a.id=?2  ")
    @Modifying
    int reducePlayMoneyById(BigDecimal amount, Long id);

    /**
     * 减少余额 可为负
     *
     * @param amount
     * @param id
     * @return
     */
    @Query(value = "update Assets a set a.balance = a.balance - ?1 where a.id=?2  ")
    @Modifying
    int doSubtractBalanceById(BigDecimal amount, Long id);


}
