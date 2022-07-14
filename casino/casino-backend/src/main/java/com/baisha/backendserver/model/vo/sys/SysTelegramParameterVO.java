package com.baisha.backendserver.model.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-电报配置")
public class SysTelegramParameterVO {

    @ApiModelProperty(value = "ID （如果获取信息接口没有则不传, 设置成功后会返回ID，下次必传 ）")
    private Long id;

    @ApiModelProperty(value = "唯一财务", required = true)
    private String onlyFinance;

    @ApiModelProperty(value = "唯一财务TG用户ID", required = true)
    private String onlyFinanceTgId;

    @ApiModelProperty(value = "唯一客服TG用户ID", required = true)
    private String onlyCustomerServiceTgId;

    @ApiModelProperty(value = "唯一客服", required = true)
    private String onlyCustomerService;

    @ApiModelProperty(value = "开始下注图片路径", required = true)
    private String startBetPicUrl;

    //@ApiModelProperty(value = "开始下注倒计时(秒) ", required = true)
    //private Integer startBetSeventySeconds;

    @ApiModelProperty(value = "开始下注倒计时路径 ", required = true)
    private String seventySecondsUrl;

    @ApiModelProperty(value = "博彩官方频道", required = true)
    private String officialGamingChannel;

    @ApiModelProperty(value = "开牌图片路径", required = true)
    private String openCardUrl;


}
