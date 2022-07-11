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
 * @author yihui
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@org.hibernate.annotations.Table(appliesTo = "bs_odds", comment = "游戏玩法赔率")
@Table(name = "BsOdds", indexes = {@Index(columnList = "gameCode"), @Index(columnList = "ruleCode")})
@ApiModel(value = "游戏玩法赔率对象", description = "游戏玩法赔率")
public class BsOdds extends BaseEntity {

    private static final long serialVersionUID = 8497677317292208992L;

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


}
