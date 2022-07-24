package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "TG群-下注机器人-绑定状态")
public class TgChatBetBotBindingVO {

    @ApiModelProperty(value = "TgBetBot对象-主键")
    private Long tgBetBotId;

    @ApiModelProperty(value = "TgBetBot对象-机器人名称")
    private String tgBetBotName;

    @ApiModelProperty(value = "TgBetBot对象-机器人手机号")
    private String tgBetBotPhone;

    @ApiModelProperty(value = "1:已绑定 0:未绑定")
    private Integer bindingStatus;
}
