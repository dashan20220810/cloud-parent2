package com.baisha.userserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Version;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "assets", comment = "会员资产")
@ApiModel(value = "用户中心-个人资产")
public class Assets extends BaseEntity {

    @ApiModelProperty(value = "会员ID")
    @Column(unique = true, columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @ApiModelProperty(value = "余额")
    @Column(columnDefinition = "decimal(16,2) comment '余额'")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(value = "冻结余额")
    @Column(columnDefinition = "decimal(16,2) comment '冻结余额'")
    private BigDecimal freezeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "打码量")
    @Column(columnDefinition = "decimal(16,2) comment '打码量'")
    private BigDecimal playMoney = BigDecimal.ZERO;

    @Version
    @Column(name = "version")
    private Integer version = 1;

}
