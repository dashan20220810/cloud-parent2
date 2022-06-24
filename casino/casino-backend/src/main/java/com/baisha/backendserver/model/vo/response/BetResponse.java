package com.baisha.backendserver.model.vo.response;

import javax.persistence.Column;

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
@ApiModel(value = "Bet对象", description = "注单")
public class BetResponse{

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
}
