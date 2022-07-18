package com.baisha.backendserver.model.vo.tgBot;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Api(tags = "后台-机器人管理(自动投注)新增对象")
public class TgBotAutoAddVO {

    @ApiModelProperty(value = "机器人名称", required = true)
    private String betBotName;

    @ApiModelProperty(value = "机器人token", required = true)
    private String betBotToken;

    @ApiModelProperty(value = "投注开始时间 HH:mm:ss", required = true)
    private String betStartTime;

    @ApiModelProperty(value = "投注结束时间(大于开始时间) HH:mm:ss", required = true)
    private String betEndTime;

    @ApiModelProperty(value = "投注频率 0-10整数", required = true)
    private Integer betFrequency;

    @ApiModelProperty(value = "投注内容(接口  多个 英文,隔开)", required = true)
    private String betContents;

    @ApiModelProperty(value = "投注金额-最小倍数 1-5整数", required = true)
    private Integer minMultiple;

    @ApiModelProperty(value = "投注金额-最大倍数 6-20整数", required = true)
    private Integer maxMultiple;


}
