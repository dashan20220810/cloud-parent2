package com.baisha.backendserver.model.vo.tgGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-TG群绑定机器人")
public class TgGroupBindVO {

    @ApiModelProperty(value = "投注机器人ID 多个用英文逗号 ，隔开  ", required = true)
    private String tgBetBotIds;

    @ApiModelProperty(value = "TG群主键ID", required = true)
    private Long id;

}
