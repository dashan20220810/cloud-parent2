package com.baisha.backendserver.model.bo.tgBot;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "机器人(自动投注)-分页对象")
public class TgBotAutoPageBO extends BaseBO {

    @ApiModelProperty(value = "TG用户ID(必须要真实的 1-15位) ", required = true)
    private String betBotId;

    @ApiModelProperty(value = "机器人名称")
    private String betBotName;

    @ApiModelProperty(value = "手机号", required = true)
    private String betBotPhone;

    @ApiModelProperty(value = "投注开始时间")
    private String betStartTime;

    @ApiModelProperty(value = "投注结束时间")
    private String betEndTime;

    @ApiModelProperty(value = "投注频率")
    private Integer betFrequency;

    @ApiModelProperty(value = "投注内容")
    private String betContents;

    @ApiModelProperty(value = "投注内容名称")
    private String betContentsName;

    @ApiModelProperty(value = "投注金额-最小倍数")
    private Integer minMultiple;

    @ApiModelProperty(value = "投注金额-最大倍数")
    private Integer maxMultiple;

    @ApiModelProperty(value = "状态 0禁用 1启用")
    private Integer status;


}
