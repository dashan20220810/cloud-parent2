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

	@ApiModelProperty("user_id")
    @Column(name="user_id")
	private Long userId;

	@ApiModelProperty("tg_chat_id")
    @Column(name="tg_chat_id")
	private Long tgChatId;

    @ApiModelProperty("下注类型")
    @Column(name="bet_option")
	private BetOption betOption;

    @ApiModelProperty("下注金额")
	private Long amount;

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
    	
    	if ( bet.getUserId()==null ) {
    		return false;
    	}

    	if ( bet.getBetOption() == null ) {
    		return false;
    	}

    	if ( bet.getAmount()==null || bet.getAmount()<=0L ) {
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
    public static boolean checkRequestForGs ( Bet bet, boolean isTgRequest ) {
    	
    	if ( checkRequest(bet)==false ) {
    		return false;
    	}

    	if ( StringUtils.isBlank(bet.getOrderNo()) ) {
    		return false;
    	}
    	
    	if ( bet.getStatus()==null ) {
    		return false;
    	}
    	
    	if ( isTgRequest && bet.getTgChatId()==null ) {
    		return false;
    	}
    	
    	return true;
    }
    
}
