package com.baisha.backendserver.model.bo.order;

import com.baisha.modulecommon.enums.BetOption;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;


/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Bet对象", description = "注单")
public class BetPageBO {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("下注用户ID")
    private Long userId;

    @ApiModelProperty("下注用户名")
    private String userName;

    @ApiModelProperty("tg_chat_id")
    private Long tgChatId;

    @ApiModelProperty("下注类型")
    private String betOption;

    @ApiModelProperty("下注类型名称")
    private String betOptionName;

    @ApiModelProperty("下注金额")
    private Long amount;

    @ApiModelProperty("客户端IP")
    private String clientIP;

    @ApiModelProperty("游戏轮号")
    private String noRun;

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功)")
    private Integer status;
}
