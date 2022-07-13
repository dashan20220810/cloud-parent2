package com.baisha.backendserver.model.bo.assets;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-调整类型返回对象")
public class AdjustmentTypeBO {

    @ApiModelProperty(value = "充值-调整类型")
    private List<OrderAdjustmentTypeBO> charge;

    @ApiModelProperty(value = "提现-调整类型")
    private List<OrderAdjustmentTypeBO> withdraw;


}
