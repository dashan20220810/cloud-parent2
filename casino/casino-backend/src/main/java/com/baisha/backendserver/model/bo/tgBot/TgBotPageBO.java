package com.baisha.backendserver.model.bo.tgBot;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "后台-机器人返回对象")
public class TgBotPageBO extends BaseBO {

    @ApiModelProperty("机器人名称")
    private String botName;

    @ApiModelProperty("机器人token")
    private String botToken;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;
}
