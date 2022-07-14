package com.baisha.backendserver.model.bo.order;

import com.baisha.backendserver.model.bo.BaseBO;
import com.baisha.modulecommon.enums.BetOption;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Bet对象", description = "注单")
public class BetPageBO extends BaseBO {

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("下注用户ID")
    private Long userId;

    @ApiModelProperty("下注用户名")
    private String userName;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("tg_chat_id")
    private Long tgChatId;

    //@ApiModelProperty("下注类型")
    //private String betOption;

    //@ApiModelProperty("下注类型名称")
    //private String betOptionName;

    //@ApiModelProperty("下注金额")
    //private Long amount;

    @ApiModelProperty("客户端IP")
    private String clientIP;

    //@ApiModelProperty("游戏轮号")
    //private String noRun;

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty("注单状态(1.下注 2.结算)")
    private Integer status;

    @ApiModelProperty(value = "输赢金额")
    private BigDecimal winAmount;

    @ApiModelProperty(value = "注单输赢结果(含派彩)")
    private BigDecimal finalAmount;

    @ApiModelProperty(value = "结算时间")
    private String settleTime;

    @ApiModelProperty("注单状态名称")
    private String statusName;

    @ApiModelProperty(value = "下注金额详细")
    private String totalAmount;

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

    @ApiModelProperty("备注(结算)")
    private String settleRemark;

}
