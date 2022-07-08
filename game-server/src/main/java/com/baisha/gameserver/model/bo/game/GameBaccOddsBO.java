package com.baisha.gameserver.model.bo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "gs-百家乐赔率")
public class GameBaccOddsBO {

    @ApiModelProperty(value = "闲-赔率")
    private BigDecimal x = BigDecimal.valueOf(1.0);

    @ApiModelProperty(value = "庄-赔率")
    private BigDecimal z = BigDecimal.valueOf(0.95);

    @ApiModelProperty(value = "和-赔率")
    private BigDecimal h = BigDecimal.valueOf(8.0);

    @ApiModelProperty(value = "闲对-赔率")
    private BigDecimal xd = BigDecimal.valueOf(11.0);

    @ApiModelProperty(value = "庄对-赔率")
    private BigDecimal zd = BigDecimal.valueOf(11.0);

    @ApiModelProperty(value = "幸运6(ss2)-赔率")
    private BigDecimal ss2 = BigDecimal.valueOf(12.0);

    @ApiModelProperty(value = "幸运6(ss3)-赔率")
    private BigDecimal ss3 = BigDecimal.valueOf(20.0);


}
