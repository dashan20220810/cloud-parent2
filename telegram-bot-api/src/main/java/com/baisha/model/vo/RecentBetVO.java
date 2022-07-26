package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "展示最近注单")
public class RecentBetVO {

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty(value = "下注内容")
    private String betCommand;

    @ApiModelProperty(value = "下注金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "输赢金额")
    private BigDecimal winAmount;

    @ApiModelProperty(value = "输赢金额-String")
    private String winStrAmount;

    @ApiModelProperty("下注金额-庄")
    private Long amountZ;

    @ApiModelProperty("下注金额-闲")
    private Long amountX;

    @ApiModelProperty("下注金额-和")
    private Long amountH;

    @ApiModelProperty("下注金额-庄对")
    private Long amountZd;

    @ApiModelProperty("下注金额-闲对")
    private Long amountXd;

    @ApiModelProperty("下注金额-幸运六")
    private Long amountSs;
}
