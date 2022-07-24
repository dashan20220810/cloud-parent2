package com.baisha.backendserver.model.bo.tgGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "后台-TG群下的机器人绑定关系")
public class TgGroupBindsBO {

    @ApiModelProperty(value = "1:已绑定 0:未绑定")
    private Integer bindingStatus;

    @ApiModelProperty(value = "投注机器人ID")
    private Long tgBetBotId;

    @ApiModelProperty(value = "手机号（显示用）")
    private String tgBetBotPhone;

    @ApiModelProperty(value = "机器人名称")
    private String tgBetBotName;


}
