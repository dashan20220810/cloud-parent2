package com.baisha.core.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "web-限红返回对象")
@Data
public class LimitStakesVO {

    @ApiModelProperty(value="单注最低")
    private Integer minAmount = 20;

    @ApiModelProperty(value="单注最高")
    private Integer maxAmount = 15000;

    @ApiModelProperty(value="当局最高")
    private Integer maxShoeAmount = 15000;
}
