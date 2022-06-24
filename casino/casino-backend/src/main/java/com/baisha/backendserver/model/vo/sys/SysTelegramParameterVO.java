package com.baisha.backendserver.model.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-电报配置")
public class SysTelegramParameterVO {

    @ApiModelProperty(value = "ID 至少传一个参数")
    private Long id;

    @ApiModelProperty(value = "唯一财务")
    private String onlyFinance;

    @ApiModelProperty(value = "唯一客服")
    private String onlyCustomerService;

    @ApiModelProperty(value = "开始下注图片路径")
    private String startBetPicUrl;

}
