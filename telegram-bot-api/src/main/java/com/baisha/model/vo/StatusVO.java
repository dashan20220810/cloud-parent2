package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "TG机器人-状态对象")
public class StatusVO {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "状态(0禁用 1启用)", required = true)
    private Integer status;
}
