package com.baisha.userserver.vo.balance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户中心-上/下分请求请对象")
public class BalanceVO {

    @ApiModelProperty(value = "用户名(长度3-30位,只能输入字母或数字或_)", required = true)
    private String userName;

    @ApiModelProperty(value = "金额", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "收支类型(1收入 2支出)", required = true)
    private Integer balanceType;

    @ApiModelProperty(value = "备注(1-100位)")
    private String remark;

}
