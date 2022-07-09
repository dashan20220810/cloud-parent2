package com.baisha.gameserver.vo.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "game-Bet返回对象", description = "注单")
public class BetResponseVO {

	@ApiModelProperty("id")
	private Long id;

    @ApiModelProperty("订单编号")
	private String orderNo;

	@ApiModelProperty("user_id")
	private Long userId;

	@ApiModelProperty("nick_name")
	private String nickName;

	@ApiModelProperty("tg_chat_id")
	private Long tgChatId;

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

    @ApiModelProperty("客户端IP")
    private String clientIP;

    @ApiModelProperty("游戏轮号")
    private String noRun;

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功)")
    private Integer status;

    @ApiModelProperty(value = "输赢金额")
    private BigDecimal winAmount;
    
    public Long getTotalAmount() {
    	return amountZ +amountX +amountH +amountZd +amountXd +amountSs;
    }
}
