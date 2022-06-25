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

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "状态(1是,0否)", required = true)
    private Integer status;


}
