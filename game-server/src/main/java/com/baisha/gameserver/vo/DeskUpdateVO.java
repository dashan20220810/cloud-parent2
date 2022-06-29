package com.baisha.gameserver.vo;

import org.springframework.beans.BeanUtils;

import com.baisha.gameserver.enums.GameType;
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

    @ApiModelProperty(required=true, value = "桌台名称")
    private String name;

    @ApiModelProperty(required=true, value = "内网IP(1-20位)")
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址")
    private String videoAddress;

    @ApiModelProperty(required=true, value = "状态 1 正常 ，0禁用")
    private Integer status;

    @ApiModelProperty(required=true, value = "游戏编码: bacc百家乐")
    private GameType gameCode;

    public Desk generateDesk() {
    	Desk result = new Desk();
    	BeanUtils.copyProperties(this, result);
    	result.setGameCode(this.getGameCode().getCode());
    	return result;
    }
}
