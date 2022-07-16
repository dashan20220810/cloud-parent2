package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "配置信息")
public class ConfigInfo {

    @ApiModelProperty(value = "唯一财务")
    private String onlyFinance = "";

    @ApiModelProperty(value = "唯一客服")
    private String onlyCustomerService = "";

    @ApiModelProperty(value = "唯一财务TG用户ID")
    private String onlyFinanceTgId = "";

    @ApiModelProperty(value = "唯一客服TG用户ID")
    private String onlyCustomerServiceTgId = "";

    @ApiModelProperty(value = "白沙集团-博彩官方频道")
    private String officialGamingChannel = "";
}
