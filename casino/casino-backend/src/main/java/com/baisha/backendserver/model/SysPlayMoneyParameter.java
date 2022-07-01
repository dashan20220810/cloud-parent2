package com.baisha.backendserver.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "sys_play_money_parameter", comment = "打码量倍率参数配置")
public class SysPlayMoneyParameter extends BaseEntity {

    @Column(columnDefinition = "decimal(10,2) comment '充值倍率'")
    private BigDecimal recharge = BigDecimal.ONE;


}
