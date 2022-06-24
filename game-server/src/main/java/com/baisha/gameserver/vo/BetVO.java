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
	
	@ApiModelProperty(value="來自tg請求")
    private boolean isTgRequest;

    @ApiModelProperty(required=true, value="订单编号")
    private String orderNo;

	@ApiModelProperty(required=true, value="tg_chat_id")
	private Long tgChatId;

	@ApiModelProperty(required=true, value="user_id")
	private Long userId;

	@ApiModelProperty(required=true, value="user_name")
	private String userName;

    @ApiModelProperty(required=true, value="下注类型", notes="ZD庄对,XD闲对,Z庄,X闲,H和,D对,SS超六")
	private BetOption betOption;

    @ApiModelProperty(required=true, value="下注金额")
	private Long amount;

    @ApiModelProperty(required=true, value="游戏轮号")
    private String noRun;

    @ApiModelProperty(required=true, value="游戏局号")
    private String noActive;

    @ApiModelProperty(required=true, value="订单状态")
    private Integer status;

    @ApiModelProperty(required=true, value="IP")
    private String clientIP;

    public Bet generateBet() {
    	Bet result = new Bet();
    	BeanUtils.copyProperties(this, result);
    	result.setBetOption(this.getBetOption().toString());
    	return result;
    }
}