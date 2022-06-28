package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "TG用户下注")
public class TgBetVO {

    @ApiModelProperty(value = "命令")
    private String command;

    @ApiModelProperty(value = "金额")
    private Long amount;
}
