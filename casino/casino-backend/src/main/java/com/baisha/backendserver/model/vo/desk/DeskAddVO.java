package com.baisha.backendserver.model.vo.desk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-桌台新增请求对象")
public class DeskAddVO {

    @ApiModelProperty(value = "桌台编码(1-10位 例如G26)", required = true)
    private String deskCode;

    @ApiModelProperty(value = "内网IP(1-20位)", required = true)
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址")
    private String videoAddress;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用", required = true)
    private Integer status;

    @ApiModelProperty(value = "游戏编码", required = true)
    private String gameCode;

}
