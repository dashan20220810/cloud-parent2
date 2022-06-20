package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

import com.baisha.modulecommon.enums.BetOption;

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
@ApiModel(value = "Bet对象", description = "注单")
public class Bet extends BaseEntity{

    private static final long serialVersionUID = -843697330512478843L;

    @ApiModelProperty("订单编号")
    @Column(name="order_no")
	private String orderNo;

	@ApiModelProperty("user_name")
    @Column(name="user_name")
	private String userName;
    
    @ApiModelProperty("下注类型")
    @Column(name="bet_option")
	private BetOption betOption;

    @ApiModelProperty("下注金额")
	private Long amount;

    @ApiModelProperty("客户端类型: 1网版, 2手机版, 3飞机")
    @Column(name="client_type")
	private String clientType;

    @ApiModelProperty("客户端IP")
    @Column(name="client_ip")
    private String clientIP;

    @ApiModelProperty("游戏轮号")
    @Column(name="no_run")
    private String noRun;

    @ApiModelProperty("游戏局号")
    @Column(name="no_active")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功)")
    private Integer status;
    
    /**
     * 检核下注请求
     * @param bet
     * @return
     */
    public static boolean checkRequest ( Bet bet ) {
    	
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

    	if ( StringUtils.isBlank(bet.getClientIP()) ) {
    		return false;
    	}

    	return true;
    }
    
    /**
     * 检核下注请求 game server
     * @param bet
     * @return
     */
    public static boolean checkRequestForGs ( Bet bet ) {
    	
    	if ( checkRequest(bet)==false ) {
    		return false;
    	}

    	if ( StringUtils.isBlank(bet.getOrderNo()) ) {
    		return false;
    	}
    	
    	if ( bet.getStatus()==null ) {
    		return false;
    	}
    	
    	return true;
    }
}
