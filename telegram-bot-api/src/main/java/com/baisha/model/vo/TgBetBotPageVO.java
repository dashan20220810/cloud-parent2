package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "投注机器人-分页对象")
public class TgBetBotPageVO extends PageVO {

    @ApiModelProperty(value = "机器人名称")
    private String betBotName;
}
