package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@ApiModel(value = "GS-BetResult对象", description = "开牌结果")
public class BetResult extends BaseEntity{
	
	private static final long serialVersionUID = 7547541941146899081L;

	@ApiModelProperty("table_id")
    @Column(name="table_id", nullable=false)
	private Long tableId;

    @ApiModelProperty("游戏局号")
    @Column(name="no_active", nullable=false, columnDefinition="VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("下注类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,D对,SS幸运六,SB三宝")
    @Column(name="bet_option", nullable=false
    	, columnDefinition="VARCHAR(10) COMMENT '下注类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,D对,SS幸运六,SB三宝'")
	private String betOption;
}
