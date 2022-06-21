package com.baisha.userserver.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Slf4j
@Data
@Entity
public class Assets extends BaseEntity {

    /**
     * 会员ID
     */
    @Column(unique = true, precision = 20)
    private Long userId;
    /**
     * 余额
     */
    @Column(precision = 16, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    /**
     * 冻结余额
     */
    @Column(precision = 16, scale = 2)
    private BigDecimal freezeAmount = BigDecimal.ZERO;


}
