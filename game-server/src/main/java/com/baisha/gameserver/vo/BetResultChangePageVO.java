package com.baisha.gameserver.vo;

import com.baisha.modulecommon.PageVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "web-重开牌分页对象")
public class BetResultChangePageVO extends PageVO {

    @ApiModelProperty("桌台id")
	private Long tableId;

    @ApiModelProperty("游戏局号")
    private String noActive;
}
