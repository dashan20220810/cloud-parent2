package com.baisha.backendserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-上/下分请求请对象")
public class BalanceVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long id;

    @ApiModelProperty(value = "金额 大于0的整数(1-7位)", required = true)
    private Integer amount;

    /*@ApiModelProperty(value = "收支类型(1收入 2支出)", required = true)
    private Integer balanceType;*/

    @ApiModelProperty(value = "备注(1-100位)")
    private String remark;

}
