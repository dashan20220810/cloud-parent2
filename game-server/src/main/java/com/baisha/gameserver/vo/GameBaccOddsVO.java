package com.baisha.gameserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@ApiModel(value = "gs-百家乐赔率设置")
public class GameBaccOddsVO {

    @ApiModelProperty(value = "游戏编码", required = true)
    private String gameCode;

    @ApiModelProperty(value = "闲-赔率(大于0,支持2位小数)", required = true)
    private BigDecimal x;
    @ApiModelProperty(value = "闲-最大限红", required = true)
    private Integer xMaxAmount;
    @ApiModelProperty(value = "闲-最小限红", required = true)
    private Integer xMinAmount;

    @ApiModelProperty(value = "庄-赔率(大于0,支持2位小数)", required = true)
    private BigDecimal z;
    @ApiModelProperty(value = "庄-最大限红", required = true)
    private Integer zMaxAmount;
    @ApiModelProperty(value = "庄-最小限红", required = true)
    private Integer zMinAmount;

    @ApiModelProperty(value = "和-赔率(大于0,支持2位小数)", required = true)
    private BigDecimal h;
    @ApiModelProperty(value = "和-最大限红", required = true)
    private Integer hMaxAmount;
    @ApiModelProperty(value = "和-最小限红", required = true)
    private Integer hMinAmount;

    @ApiModelProperty(value = "闲对-赔率(大于0,支持2位小数)", required = true)
    private BigDecimal xd;
    @ApiModelProperty(value = "闲对-最大限红", required = true)
    private Integer xdMaxAmount;
    @ApiModelProperty(value = "闲对-最小限红", required = true)
    private Integer xdMinAmount;

    @ApiModelProperty(value = "庄对-赔率(大于0,支持2位小数)", required = true)
    private BigDecimal zd;
    @ApiModelProperty(value = "庄对-最大限红", required = true)
    private Integer zdMaxAmount;
    @ApiModelProperty(value = "庄对-最小限红", required = true)
    private Integer zdMinAmount;

    @ApiModelProperty(value = "幸运6-赔率(ss2(大于0,支持2位小数))", required = true)
    private BigDecimal ss2;

    @ApiModelProperty(value = "幸运6-赔率(ss3(大于0,支持2位小数))", required = true)
    private BigDecimal ss3;

    @ApiModelProperty(value = "幸运6-最大限红", required = true)
    private Integer ssMaxAmount;
    @ApiModelProperty(value = "幸运6-最小限红", required = true)
    private Integer ssMinAmount;


}
