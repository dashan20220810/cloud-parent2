package com.baisha.backendserver.bo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-电报配置返回对象")
public class SysTelegramParameterBO {

    @ApiModelProperty(value = "唯一财务")
    private String onlyFinance;

    @ApiModelProperty(value = "唯一客服")
    private String onlyCustomerService;

    @ApiModelProperty(value = "开始下注图片路径")
    private String startBetPicUrl;

    @ApiModelProperty(value = "显示开始下注图片路径")
    private String startBetPicUrlShow;
}