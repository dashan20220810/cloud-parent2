package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

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
@ApiModel(value = "GS-Bet对象", description = "注单")
public class Bet extends BaseEntity{

    private static final long serialVersionUID = -843697330512478843L;

    @ApiModelProperty("订单编号")
    @Column(name="order_no", nullable=false, columnDefinition="VARCHAR(50) COMMENT '订单编号'")
	private String orderNo;

	@ApiModelProperty("user_id")
    @Column(name="user_id", nullable=false)
	private Long userId;

	@ApiModelProperty("tg_chat_id")
    @Column(name="tg_chat_id", columnDefinition="BIGINT COMMENT 'telegram群id'")
	private Long tgChatId;

    @ApiModelProperty("下注类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,D对,SS幸运六,SB三宝")
    @Column(name="bet_option", nullable=false
    	, columnDefinition="VARCHAR(10) COMMENT '下注类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,D对,SS幸运六,SB三宝'")
	private String betOption;

    @ApiModelProperty("下注金额")
    @Column(name="amount", columnDefinition="BIGINT COMMENT '下注金额'", nullable=false)
	private Long amount;

    @ApiModelProperty("客户端IP")
    @Column(name="client_ip", columnDefinition="VARCHAR(20) COMMENT '客户端IP'")
    private String clientIP;

    @ApiModelProperty("游戏轮号")
    @Column(name="no_run", columnDefinition="VARCHAR(50) COMMENT '游戏轮号'")
    private String noRun;

    @ApiModelProperty("游戏局号")
    @Column(name="no_active", nullable=false, columnDefinition="VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功)")
    @Column(name="status", nullable=false, columnDefinition="INT COMMENT '注單狀態(1.下注成功)'")
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

    	if ( StringUtils.isBlank(bet.getBetOption()) ) {
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
