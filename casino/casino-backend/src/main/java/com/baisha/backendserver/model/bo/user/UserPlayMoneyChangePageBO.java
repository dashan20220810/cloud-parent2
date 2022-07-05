package com.baisha.backendserver.model.bo.user;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-用户打码量变动分页返回对象")
public class UserPlayMoneyChangePageBO extends BaseBO {
    @ApiModelProperty(value = "会员ID")
    private Long userId;

    @ApiModelProperty(value = "关联ID")
    private Long relateId;

    @ApiModelProperty(value = "变动类型名称")
    private String changeTypeName;
    @ApiModelProperty(value = "变动类型  1充值 2结算")
    private Integer changeType;

    @ApiModelProperty(value = "收支类型 1收入 2支出")
    private Integer playMoneyType;
    @ApiModelProperty(value = "收支类型名称")
    private String playMoneyTypeName;

    @ApiModelProperty(value = "交易前金额")
    private BigDecimal beforeAmount;

    @ApiModelProperty(value = "变化金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易后金额")
    private BigDecimal afterAmount;

    @ApiModelProperty(value = "备注信息")
    private String remark;

}
