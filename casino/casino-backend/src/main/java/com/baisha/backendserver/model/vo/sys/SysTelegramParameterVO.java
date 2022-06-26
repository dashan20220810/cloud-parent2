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

    @ApiModelProperty(value = "ID （如果获取信息接口没有则不传, 设置成功后会返回ID，下次必传 ）")
    private Long id;

    @ApiModelProperty(value = "唯一财务", required = true)
    private String onlyFinance;

    @ApiModelProperty(value = "唯一客服", required = true)
    private String onlyCustomerService;

    @ApiModelProperty(value = "开始下注图片路径", required = true)
    private String startBetPicUrl;

}
