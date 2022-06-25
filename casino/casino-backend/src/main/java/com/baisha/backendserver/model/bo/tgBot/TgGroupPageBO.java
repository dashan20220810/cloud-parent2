package com.baisha.backendserver.model.bo.tgBot;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel(value = "后台-机器人对应的群对象")
public class TgGroupPageBO extends BaseBO {

    @ApiModelProperty("群id")
    private String chatId;

    @ApiModelProperty("群名称")
    private String chatName;

    @ApiModelProperty("机器人名称")
    private String botName;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;


}
