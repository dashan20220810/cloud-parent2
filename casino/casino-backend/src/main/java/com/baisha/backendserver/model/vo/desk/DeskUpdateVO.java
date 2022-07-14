package com.baisha.backendserver.model.vo.desk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-桌台更新请求对象")
public class DeskUpdateVO {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    /*@ApiModelProperty(value = "桌台编码(1-10位 例如G26)", required = true)
    private String deskCode;*/

    @ApiModelProperty(value = "桌台名称(1-30位 例如 百家乐一台)")
    private String name;

    @ApiModelProperty(value = "内网IP(1-20位)", required = true)
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址(远景)", required = true)
    private String videoAddress;

    @ApiModelProperty(value = "游戏视频地址(近景)", required = true)
    private String nearVideoAddress;

    @ApiModelProperty(value = "游戏视频地址(开牌结果)", required = true)
    private String closeVideoAddress;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用", required = true)
    private Integer status;

    @ApiModelProperty(value = "游戏编码", required = true)
    private String gameCode;

}
