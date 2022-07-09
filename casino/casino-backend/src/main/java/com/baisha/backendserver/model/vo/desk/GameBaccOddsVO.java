package com.baisha.backendserver.model.vo.desk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "后台-百家乐赔率设置请求对象")
public class GameBaccOddsVO {

    @ApiModelProperty(value = "游戏编码", required = true)
    private String gameCode;

    @ApiModelProperty(value = "闲-赔率(大于0小于100,支持2位小数)", required = true)
    private BigDecimal x;

    @ApiModelProperty(value = "庄-赔率(大于0小于100,支持2位小数)", required = true)
    private BigDecimal z;

    @ApiModelProperty(value = "和-赔率(大于0小于100,支持2位小数)", required = true)
    private BigDecimal h;

    @ApiModelProperty(value = "闲对-赔率(大于0小于100,支持2位小数)", required = true)
    private BigDecimal xd;

    @ApiModelProperty(value = "庄对-赔率(大于0小于100,支持2位小数)", required = true)
    private BigDecimal zd;

    @ApiModelProperty(value = "幸运6-赔率(ss2(大于0小于100,支持2位小数))", required = true)
    private BigDecimal ss2;

    @ApiModelProperty(value = "幸运6-赔率(ss3(大于0小于100,支持2位小数))", required = true)
    private BigDecimal ss3;
}
