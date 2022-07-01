package com.baisha.backendserver.model.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-打码量倍率参数配置请求对象")
public class SysPlayMoneyParameterVO {

    @ApiModelProperty(value = "ID （如果获取信息接口没有则不传, 设置成功后会返回ID，下次必传 ）")
    private Long id;
    
    @ApiModelProperty(value = "充值倍率(1-10数字 支持2位小数)", required = true)
    private BigDecimal recharge;

}
