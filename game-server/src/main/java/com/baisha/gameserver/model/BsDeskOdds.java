package com.baisha.gameserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@org.hibernate.annotations.Table(appliesTo = "bs_desk_odds", comment = "游戏玩法赔率")
@Table(name = "BsDeskOdds", indexes = {@Index(columnList = "gameCode"), @Index(columnList = "ruleCode"), @Index(columnList = "deskId")})
@ApiModel(value = "桌台游戏玩法赔率对象", description = "桌台游戏玩法赔率")
public class BsDeskOdds extends BaseEntity {

    @ApiModelProperty(value = "桌台ID")
    @Column(columnDefinition = "bigint(11) comment '桌台ID'")
    private Long deskId;

    @ApiModelProperty(value = "游戏编码")
    @Column(columnDefinition = "varchar(20) comment '游戏编码'")
    private String gameCode;

    @ApiModelProperty(value = "玩法编码")
    @Column(columnDefinition = "varchar(20) comment '玩法编码'")
    private String ruleCode;

    @ApiModelProperty(value = "玩法名称")
    @Column(columnDefinition = "varchar(20) comment '玩法名称'")
    private String ruleName;

    @ApiModelProperty(value = "赔率")
    @Column(columnDefinition = "decimal(10,2) comment '赔率(支持2位小数)' ")
    private BigDecimal odds;

    // 最大限红
    @ApiModelProperty(value = "最大限红")
    @Column(columnDefinition = "varchar(20) comment '最大限红'")
    private Integer maxAmount;

    // 最小限红
    @ApiModelProperty(value = "最小限红")
    @Column(columnDefinition = "varchar(20) comment '最小限红'")
    private Integer minAmount;


}
