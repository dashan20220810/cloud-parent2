package com.baisha.backendserver.model.bo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysPlayMoneyParameterBO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "充值倍率(1-10数字 支持2位小数)")
    private BigDecimal recharge;

}
