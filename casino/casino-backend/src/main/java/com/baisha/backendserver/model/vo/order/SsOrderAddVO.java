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

    @ApiModelProperty(value = "TG用户ID")
    private String tgUserId;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "调整类型")
    private Integer adjustmentType;

    @ApiModelProperty(value = "流水倍数 >=0 (0-100 支持2位小数)", required = true)
    private BigDecimal flowMultiple;

    @ApiModelProperty(value = "附件key")
    private String fileKey;



    private String createBy;

    private String updateBy;

}
