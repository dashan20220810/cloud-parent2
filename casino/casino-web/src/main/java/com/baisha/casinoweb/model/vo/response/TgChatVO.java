package com.baisha.casinoweb.model.vo.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "web-TgChat对象", description = "TG群")
public class TgChatVO implements Serializable {

    private static final long serialVersionUID = 2116155825235050626L;

	@ApiModelProperty(value = "群id",required = true)
    private Long chatId;

    @ApiModelProperty(value="机器人id",required = true)
    private Long botId;

    @ApiModelProperty(value="游戏桌台id",required = true)
    private Long tableId;

    @ApiModelProperty(value="群名称",required = true)
    private String chatName;

    @ApiModelProperty(value="机器人名称",required = true)
    private String botName;

    //业务属性
    @ApiModelProperty(value="状态 0禁用 1启用",required = true)
    private Integer status;

    @ApiModelProperty(name = "单注限红最低", required = true)
    private Integer minAmount;

    @ApiModelProperty(name = "单注限红最高", required = true)
    private Integer maxAmount;

    @ApiModelProperty(name = "当局最高", required = true)
    private Integer maxShoeAmount;

}
