package com.baisha.casinoweb.model.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "web-系统属性返回对象", description = "系统属性")
public class PropResponseVO {

    @ApiModelProperty("唯一财务")
	private String onlyFinance;

    @ApiModelProperty("客服")
	private String onlyCustomerService;

    @ApiModelProperty("官方频道")
	private String officialGamingChannel;
}
