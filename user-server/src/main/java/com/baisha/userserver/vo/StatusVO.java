package com.baisha.userserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户中心-状态对象")
public class StatusVO {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "状态(1正常,2禁用)", required = true)
    private Integer status;

}
