package com.baisha.backendserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-增加/减少打码量请求请对象")
public class PlayMoneyVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long id;

    @ApiModelProperty(value = "打码量", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "收支类型(1收入 2支出)", required = true)
    private Integer playMoneyType;

    @ApiModelProperty(value = "备注(1-100位)")
    private String remark;

}
