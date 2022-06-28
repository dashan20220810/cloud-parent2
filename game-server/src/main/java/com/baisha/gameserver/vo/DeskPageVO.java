package com.baisha.gameserver.vo;

import com.baisha.gameserver.enums.GameType;
import com.baisha.modulecommon.PageVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "桌台分页对象")
public class DeskPageVO extends PageVO {

    @ApiModelProperty(value = "桌台编码(1-10位 例如G26)")
    private String deskCode;

    @ApiModelProperty(value = "桌台名称")
    private String name;

    @ApiModelProperty(value = "内网IP(1-20位)")
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址")
    private String videoAddress;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;

    @ApiModelProperty(value = "游戏编码: bacc百家乐")
    private GameType gameCode;
}
