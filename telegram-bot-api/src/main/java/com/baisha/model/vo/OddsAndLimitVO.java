package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "玩法赔率/限红")
public class OddsAndLimitVO {

    @ApiModelProperty("游戏编号")
    private String gameCode;

    @ApiModelProperty(value = "下注内容")
    private String ruleCode;

    @ApiModelProperty(value = "下注内容-名称")
    private String ruleName;

    @ApiModelProperty(value = "玩法赔率")
    private BigDecimal odds;

    @ApiModelProperty("最大限红")
    private Long maxAmount;

    @ApiModelProperty("最小限红")
    private Long minAmount;
}
