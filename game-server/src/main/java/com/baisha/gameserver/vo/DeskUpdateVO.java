package com.baisha.gameserver.vo;

import org.springframework.beans.BeanUtils;

import com.baisha.gameserver.model.Desk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author: alvin
 */
@Data
@ApiModel(value = "桌台更新请求对象")
public class DeskUpdateVO {

    @ApiModelProperty(required = true, value = "桌台名称")
    private String name;

    @ApiModelProperty(required = true, value = "内网IP(1-20位)")
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址")
    private String videoAddress;

    @ApiModelProperty(value = "游戏视频地址(近景)")
    private String nearVideoAddress;

    @ApiModelProperty(value = "游戏视频地址(开牌结果)")
    private String closeVideoAddress;

    @ApiModelProperty(required = true, value = "状态 1 正常 ，0禁用")
    private Integer status;

    @ApiModelProperty(required = true, value = "游戏编码: bacc百家乐")
    private String gameCode;

    public Desk generateDesk() {
        Desk result = new Desk();
        BeanUtils.copyProperties(this, result);
        return result;
    }
}
