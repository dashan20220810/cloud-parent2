package com.baisha.gameserver.vo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.baisha.gameserver.model.BetResultChange;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: alvin
 */
@Data
@ApiModel(value = "重开牌结果请求对象")
public class BetResultChangeVO implements Serializable {


	private static final long serialVersionUID = -2850424690359293690L;

	@ApiModelProperty(required=true, value="table_id")
	private Long tableId;

    @ApiModelProperty(required=true, value="游戏局号")
    private String noActive;

    @ApiModelProperty(value="重开牌类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,SS幸运六")
	private String finalAwardOption;

    public BetResultChange generateBetResultChange() {
    	BetResultChange result = new BetResultChange();
    	BeanUtils.copyProperties(this, result);
    	return result;
    }
}
