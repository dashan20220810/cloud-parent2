package com.baisha.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yihui
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "TG系统参数DTO")
public class SysTelegramDto {

    @ApiModelProperty(value = "唯一财务")
    private String onlyFinance;

    @ApiModelProperty(value = "唯一客服")
    private String onlyCustomerService;

    @ApiModelProperty(value = "开始下注图片路径 ")
    private String startBetPicUrl;

    @ApiModelProperty(value = "开始下注倒计时路径 ")
    private String seventySecondsUrl;

    @ApiModelProperty(value = " 博彩官方频道 ")
    private String officialGamingChannel;

    @ApiModelProperty(value = "开牌图片路径")
    private String openCardUrl;
}
