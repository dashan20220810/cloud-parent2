package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BetUserVO对象", description = "下注玩家")
public class BetUserVO {

    @ApiModelProperty(value = "玩家名称")
    private String username;

    @ApiModelProperty(value = "下注命令")
    private String betCommand;
}
