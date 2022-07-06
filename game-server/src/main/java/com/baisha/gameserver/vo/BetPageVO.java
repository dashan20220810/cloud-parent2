package com.baisha.gameserver.vo;

import com.baisha.modulecommon.PageVO;
import com.baisha.modulecommon.enums.BetOption;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "web-下注分页对象")
public class BetPageVO extends PageVO {

    @ApiModelProperty("user_name")
    private String userName;

    //@ApiModelProperty("下注类型")
    //private BetOption betOption;

    //@ApiModelProperty("客户端类型: 1网版, 2手机版, 3飞机")
    //private String clientType;

    //@ApiModelProperty("游戏轮号")
    //private String noRun;

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功)")
    private Integer status;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
