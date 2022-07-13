package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "群-分页对象")
public class TgChatPageVO extends PageVO {
    @ApiModelProperty(value = "机器人id")
    private Long botId;
}
