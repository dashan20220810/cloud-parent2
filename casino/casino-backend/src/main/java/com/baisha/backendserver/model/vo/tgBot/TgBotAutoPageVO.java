package com.baisha.backendserver.model.vo.tgBot;

import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "机器人(自动投注)-分页对象")
public class TgBotAutoPageVO extends PageVO {

    @ApiModelProperty(value = "机器人名称")
    private String betBotName;
}
