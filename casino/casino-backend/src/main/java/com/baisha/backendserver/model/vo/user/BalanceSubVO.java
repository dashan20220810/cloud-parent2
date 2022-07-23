package com.baisha.backendserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-下分请求请对象")
public class BalanceSubVO {

    @ApiModelProperty(value = "TG用户ID", required = true)
    private String tgUserId;

    @ApiModelProperty(value = "用户ID", required = true)
    private Long id;

    @ApiModelProperty(value = "金额 大于0的整数(1-7位)", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "调整类型", required = true)
    private Integer adjustmentType;

    @ApiModelProperty(value = "备注(1-50位)")
    private String remark;

    @ApiModelProperty(value = "附件key")
    private String fileKey;

}
