package com.baisha.gameserver.vo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.baisha.gameserver.model.BetResult;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: alvin
 */
@Data
@ApiModel(value = "开牌结果请求对象")
public class BetResultVO implements Serializable {

	private static final long serialVersionUID = -6150905185775229126L;

	@ApiModelProperty(required=true, value="table_id")
	private Long tableId;

    @ApiModelProperty(required=true, value="游戏局号")
    private String noActive;

    @ApiModelProperty(value="开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六")
	private String awardOption;

    public BetResult generateBetResult() {
    	BetResult result = new BetResult();
    	BeanUtils.copyProperties(this, result);
    	return result;
    }
}
