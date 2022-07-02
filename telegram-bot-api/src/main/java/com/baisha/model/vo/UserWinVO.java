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
@ApiModel(value = "UserWinVO对象", description = "输赢金额")
public class UserWinVO {

    @ApiModelProperty(value = "玩家名称")
    private String username;

    @ApiModelProperty(value = "输赢金额")
    private String winAmount;
}
