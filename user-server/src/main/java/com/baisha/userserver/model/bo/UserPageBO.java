package com.baisha.userserver.model.bo;

import com.baisha.userserver.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@ApiModel(value = "用户中心-会员分页返回对象")
public class UserPageBO extends User {

    @ApiModelProperty(value = "余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeAmount = BigDecimal.ZERO;

}
