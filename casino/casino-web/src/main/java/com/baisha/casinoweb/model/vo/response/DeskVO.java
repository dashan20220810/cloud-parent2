package com.baisha.casinoweb.model.vo.response;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "web-电报桌台")
public class DeskVO implements Serializable {


	private static final long serialVersionUID = 3318009102424855165L;
	
	private Long id;

	@ApiModelProperty(value = "桌台编码(1-10位 例如G26)")
    private String deskCode;

	@ApiModelProperty(value = "桌台名称(1-30位 )")
    private String name;

    @ApiModelProperty(value = "内网IP(1-20位)")
    private String localIp;

    @ApiModelProperty(value = "游戏视频地址(远景)")
    private String videoAddress;

    @ApiModelProperty(value = "游戏视频地址(近景)")
    private String nearVideoAddress;

    @ApiModelProperty(value = "游戏视频地址(开牌结果)")
    private String closeVideoAddress;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status = 1;

    @ApiModelProperty(value = "游戏编码")
    private String gameCode = "BACC";

}
