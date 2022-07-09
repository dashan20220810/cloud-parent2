package com.baisha.backendserver.model.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-订单对象")
public class SsOrderAddVO {

    @ApiModelProperty(value = "订单编号")
    private String orderNum;

    @ApiModelProperty(value = "会员ID")
    private Long userId;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单类型 1充值 2提现")
    private Integer orderType;

    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    private String createBy;

    private String updateBy;

}
