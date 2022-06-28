package com.baisha.backendserver.model.vo.tgBot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-机器人与TG群关系审核")
public class TgBotGroupAuditVO {

    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty(value = "状态(1是,0否)", required = true)
    private Integer status;

    @ApiModelProperty(value = "绑定游戏桌台的id")
    private Long tableId = 0L;

    //限红：单注20-15000  当局最高50000（美金)
    @ApiModelProperty(value = "单注最低")
    private Integer minAmount = 20;

    @ApiModelProperty(value = "单注最高")
    private Integer maxAmount = 15000;

    @ApiModelProperty(value = "当局最高")
    private Integer maxShoeAmount = 50000;

}
