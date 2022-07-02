package com.baisha.gameserver.vo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.baisha.gameserver.model.Bet;

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

	@ApiModelProperty(required=true, value="nick_name")
	private String nickName;

    @ApiModelProperty("下注金额庄")
    private Long amountZ = 0L;

    @ApiModelProperty("下注金额闲")
    private Long amountX = 0L;

    @ApiModelProperty("下注金额和")
    private Long amountH = 0L;

    @ApiModelProperty("下注金额庄对")
    private Long amountZd = 0L;

    @ApiModelProperty("下注金额闲对")
    private Long amountXd = 0L;

    @ApiModelProperty("下注金额幸运六")
    private Long amountSs = 0L;

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
    	return result;
    }
}