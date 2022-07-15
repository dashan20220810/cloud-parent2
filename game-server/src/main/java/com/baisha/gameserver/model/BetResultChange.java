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
@ApiModel(value = "GS-BetResultChange对象", description = "重开牌结果")
public class BetResultChange extends BaseEntity{
	

	private static final long serialVersionUID = -6815976562843237694L;

	@ApiModelProperty("table_id")
    @Column(name="table_id", nullable=false)
	private Long tableId;

    @ApiModelProperty("游戏局号")
    @Column(name="no_active", nullable=false, columnDefinition="VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六")
    @Column(name="award_option"
    	, columnDefinition="VARCHAR(10) COMMENT '开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六'")
	private String awardOption;

    @ApiModelProperty("重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六")
    @Column(name="final_award_option"
    	, columnDefinition="VARCHAR(10) COMMENT '重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六'")
	private String finalAwardOption;
    
    public boolean checkRequest () {
    	
    	if ( tableId==null ) {
    		log.warn(" tableId required!! ");
    		return false;
    	}
    	
    	if ( StringUtils.isBlank(noActive) ) {
    		log.warn(" noActive required!! ");
    		return false;
    	}
    	
    	if (StringUtils.isBlank(finalAwardOption)) {
    		log.warn(" finalAwardOption required!! ");
    		return false;
    	}
    	
    	return true;
    }
}
