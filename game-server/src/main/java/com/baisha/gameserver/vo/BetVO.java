package com.baisha.gameserver.vo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.baisha.gameserver.model.Bet;
import com.baisha.modulecommon.enums.BetOption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: alvin
 */
@Data
@ApiModel(value = "下注请求对象")
public class BetVO implements Serializable {


	private static final long serialVersionUID = -6560709467907229574L;

    @ApiModelProperty(required=true, name="订单编号")
    private String orderNo;

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

    @ApiModelProperty(required=true, name="客户端ip")
    private String clientIP;

    @ApiModelProperty(required=true, name="订单状态")
    private Integer status;

    public Bet generateBet() {
    	Bet result = new Bet();
    	BeanUtils.copyProperties(this, result);
    	return result;
    }
}