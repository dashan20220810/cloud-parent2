package com.baisha.casinoweb.vo;

import org.apache.commons.lang3.StringUtils;

import com.baisha.modulecommon.enums.BetOption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: alvin
 */
@Data
@ApiModel(value = "下注请求对象")
public class BetVO {


	@ApiModelProperty(required=true, name="user_name")
	private String userName;

    @ApiModelProperty(required=true, name="下注类型", notes="ZD庄对,XD闲对,Z庄,X闲,H和,D对,SS超六")
	private BetOption betOption;

    @ApiModelProperty(required=true, name="下注金额")
	private Long amount;
	
    /**
     * 检核下注请求
     * @param bet
     * @return
     */
    public static boolean checkRequest ( BetVO bet ) {
    	
    	if ( StringUtils.isBlank(bet.getUserName()) ) {
    		return false;
    	}

    	if ( bet.getBetOption() == null ) {
    		return false;
    	}

    	if ( bet.getAmount()==null || bet.getAmount()<=0L ) {
    		return false;
    	}

    	return true;
    }
}
