package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Slf4j
@ApiModel(value = "GS-BetResult对象", description = "开牌结果")
public class BetResult extends BaseEntity{
	
	private static final long serialVersionUID = 7547541941146899081L;

	@ApiModelProperty("table_id")
    @Column(name="table_id", nullable=false)
	private Long tableId;

    @ApiModelProperty("游戏局号")
    @Column(name="no_active", nullable=false, unique=true, columnDefinition="VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六")
    @Column(name="award_option"
    	, columnDefinition="VARCHAR(20) COMMENT '开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六'")
	private String awardOption;

    @ApiModelProperty("重开牌(0.否 1.是)")
    @Column(name = "reopen", columnDefinition = "INT COMMENT '重开牌(0.否 1.是)'")
    private Integer reopen = 0;
    
    public boolean checkRequest () {
    	
    	if ( tableId==null ) {
    		log.warn(" tableId required!! ");
    		return false;
    	}
    	
    	if ( StringUtils.isBlank(noActive) ) {
    		log.warn(" noActive required!! ");
    		return false;
    	}
    	
    	return true;
    }
}
