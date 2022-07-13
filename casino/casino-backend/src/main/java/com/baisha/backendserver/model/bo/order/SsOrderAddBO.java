package com.baisha.backendserver.model.bo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-订单信息返回对象")
public class SsOrderAddBO {

    @ApiModelProperty(value = "订单ID")
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private String orderNum;


}
