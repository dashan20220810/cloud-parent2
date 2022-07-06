package com.baisha.userserver.model.bo;

import com.baisha.userserver.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "用户中心-会员分页返回对象")
public class UserPageBO extends User {

    @ApiModelProperty(value = "余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "打码量")
    private BigDecimal playMoney = BigDecimal.ZERO;

    @ApiModelProperty(value = "邀请人会员ID")
    private Long inviteUserId;

    @ApiModelProperty(value = "邀请人TG用户ID")
    private String inviteTgUserId;

    @ApiModelProperty(value = "邀请人TG用户名")
    private String inviteTgUserName;

}
