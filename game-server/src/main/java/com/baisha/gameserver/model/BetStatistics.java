package com.baisha.gameserver.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "Bet_Statistics", indexes = {@Index(columnList = "user_id"),@Index(columnList = "statistics_date")})
@ApiModel(value = "GS-Bet对象", description = "注单")
public class BetStatistics extends BaseEntity {


    private static final long serialVersionUID = -8866767574064666756L;

    @ApiModelProperty("user_id")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ApiModelProperty("统计日期")
    @Column(name = "statistics_date", nullable = false, columnDefinition = "int(11) COMMENT '统计日期 yyyyMMdd'")
    private Integer statisticsDate;

    @ApiModelProperty(value = "输赢金额")
    @Column(columnDefinition = "decimal(16,2) comment '输赢金额'")
    private BigDecimal winAmount;

    @ApiModelProperty(value = "流水")
    @Column(columnDefinition = "decimal(16,2) comment '流水'")
    private BigDecimal flowAmount;

    @ApiModelProperty(value = "返水")
    @Column(columnDefinition = "decimal(16,2) comment '返水'")
    private BigDecimal returnAmount;
    
}
