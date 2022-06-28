package com.baisha.backendserver.model.bo.desk;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel(value = "后台-桌台分页返回对象")
public class DeskPageBO extends BaseBO {

    @ApiModelProperty(value = "桌台编码(1-10位 例如G26)")
    private String deskCode;

    @ApiModelProperty(value = "桌台名称(1-30位 例如 百家乐一台)")
    private String name;

    @ApiModelProperty(value = "内网IP(1-20位)")
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址")
    private String videoAddress;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;

    @ApiModelProperty(value = "游戏编码")
    private String gameCode;

}
