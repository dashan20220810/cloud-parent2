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
@org.hibernate.annotations.Table(appliesTo = "assets", comment = "会员资产")
public class Assets extends BaseEntity {

    @Column(unique = true, columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @Column(columnDefinition = "decimal(16,2) comment '余额'")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(columnDefinition = "decimal(16,2) comment '冻结余额'")
    private BigDecimal freezeAmount = BigDecimal.ZERO;


}
