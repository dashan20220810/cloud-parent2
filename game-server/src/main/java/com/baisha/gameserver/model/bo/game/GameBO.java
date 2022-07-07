package com.baisha.gameserver.model.bo.game;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "gs-游戏返回对象")
public class GameBO {

    @ApiModelProperty(value = "游戏编码")
    private String code;

    @ApiModelProperty(value = "游戏名称")
    private String name;
}
