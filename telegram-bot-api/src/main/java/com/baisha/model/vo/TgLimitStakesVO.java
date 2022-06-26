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
@ApiModel(value = "TgLimitStakesVO对象", description = "限红")
public class TgLimitStakesVO {
    @ApiModelProperty("单注限红最低")
    private Integer minAmount;

    @ApiModelProperty("单注限红最高")
    private Integer maxAmount;

    @ApiModelProperty("当局最高")
    private Integer maxShoeAmount;
}
