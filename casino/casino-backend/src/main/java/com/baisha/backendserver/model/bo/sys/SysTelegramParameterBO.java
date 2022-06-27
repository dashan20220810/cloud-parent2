package com.baisha.backendserver.model.bo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-电报配置返回对象")
public class SysTelegramParameterBO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "唯一财务")
    private String onlyFinance;

    @ApiModelProperty(value = "唯一客服")
    private String onlyCustomerService;

    @ApiModelProperty(value = "开始下注图片路径")
    private String startBetPicUrl;

    @ApiModelProperty(value = "显示开始下注图片路径")
    private String startBetPicUrlShow;

    @ApiModelProperty(value = "开始下注倒计时(秒) ")
    private Integer startBetSeventySeconds;

    @ApiModelProperty(value = "开始下注倒计时路径 ")
    private String seventySecondsUrl;

    @ApiModelProperty(value = "显示开始下注倒计时路径 ")
    private String seventySecondsUrlShow;

    @ApiModelProperty(value = "博彩官方频道")
    private String officialGamingChannel;


}
