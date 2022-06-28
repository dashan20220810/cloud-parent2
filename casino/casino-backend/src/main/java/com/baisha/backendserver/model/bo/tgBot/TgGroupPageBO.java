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

    @ApiModelProperty(value = "机器人id")
    private Long botId;

    @ApiModelProperty(value = "游戏桌台id")
    private Long tableId;

    @ApiModelProperty(value = "桌台编码")
    private String deskCode;

    @ApiModelProperty(value = "桌台名称")
    private String name;

    @ApiModelProperty(value = "群名称")
    private String chatName;

    @ApiModelProperty(value = "机器人名称")
    private String botName;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;

    @ApiModelProperty(value = "单注最低")
    private Integer minAmount;

    @ApiModelProperty(value = "单注最高")
    private Integer maxAmount;

    @ApiModelProperty(value = "当局最高")
    private Integer maxShoeAmount;


}
