package com.baisha.backendserver.model.bo.desk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-百家乐赔率设置")
public class GameBaccOddsBO {

    @ApiModelProperty(value = "闲-赔率")
    private BigDecimal x;

    @ApiModelProperty(value = "庄-赔率")
    private BigDecimal z;

    @ApiModelProperty(value = "和-赔率")
    private BigDecimal h;

    @ApiModelProperty(value = "闲对-赔率")
    private BigDecimal xd;

    @ApiModelProperty(value = "庄对-赔率")
    private BigDecimal zd;

    @ApiModelProperty(value = "幸运6(ss2)-赔率")
    private BigDecimal ss2;

    @ApiModelProperty(value = "幸运6(ss3)-赔率")
    private BigDecimal ss3;


}
