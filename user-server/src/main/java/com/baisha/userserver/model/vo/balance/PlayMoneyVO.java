package com.baisha.userserver.model.vo.balance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户中心-增加/减少打码量请求请对象")
public class PlayMoneyVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "打码量", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "收支类型(1收入 2支出)", required = true)
    private Integer playMoneyType;

    @ApiModelProperty(value = "备注(1-100位)")
    private String remark;

}
