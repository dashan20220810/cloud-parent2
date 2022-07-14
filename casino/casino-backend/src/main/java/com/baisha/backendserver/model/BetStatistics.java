package com.baisha.backendserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "bet_statistics", comment = "会员注单统计")
@ApiModel(value = "后台-会员注单统计")
public class BetStatistics extends BaseEntity {

    @ApiModelProperty(value = "会员ID")
    @Column(unique = true, columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @ApiModelProperty(value = "TG用户ID")
    @Column(unique = true, columnDefinition = "varchar(64) comment 'TG用户ID'")
    private String tgUserId;

    @ApiModelProperty(value = "注单数")
    @Column(columnDefinition = "int(11) comment '注单数'")
    private Integer betNum = 1;

    @ApiModelProperty(value = "累计投注额")
    @Column(columnDefinition = "decimal(16,2) comment '累计投注额'")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计盈亏")
    @Column(columnDefinition = "decimal(16,2) comment '累计盈亏'")
    private BigDecimal winAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "打码量")
    @Column(columnDefinition = "decimal(16,2) comment '打码量'")
    private BigDecimal playMoney = BigDecimal.ZERO;

    @ApiModelProperty(value = "最后投注时间")
    @Column(columnDefinition = "datetime comment '最后投注时间'")
    private Date lastBetTime;


}
