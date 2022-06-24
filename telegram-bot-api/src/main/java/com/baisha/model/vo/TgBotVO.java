package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TgBotVO对象", description = "机器人以及对应的群")
public class TgBotVO {
    @ApiModelProperty("群id")
    private String chatId;

    @ApiModelProperty("群名称")
    private String chatName;

    @ApiModelProperty("机器人名称")
    private String botName;

    @ApiModelProperty("机器人token")
    private String botToken;
}
