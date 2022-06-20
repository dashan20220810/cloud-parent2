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
    
    @ApiModelProperty(required=true, name="下注类型")
	private BetOption betOption;

    @ApiModelProperty(required=true, name="下注金额")
	private Long amount;
	
    @ApiModelProperty(required=true, name="客户端类型: 1网版, 2手机版, 3飞机")
	private String clientType;

    @ApiModelProperty(required=true, name="游戏轮号")
    private String noRun;

    @ApiModelProperty(required=true, name="游戏局号")
    private String noActive;

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

    	if ( StringUtils.isBlank(bet.getClientType()) ) {
    		return false;
    	}

    	if ( StringUtils.isBlank(bet.getNoRun()) ) {
    		return false;
    	}

    	if ( StringUtils.isBlank(bet.getNoActive()) ) {
    		return false;
    	}

    	return true;
    }
}
