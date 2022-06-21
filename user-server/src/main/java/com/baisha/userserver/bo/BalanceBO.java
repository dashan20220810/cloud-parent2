package com.baisha.userserver.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@Builder
@ApiModel(value = "用户中心-余额返回对象")
public class BalanceBO {


    @ApiModelProperty(value = "余额")
    private String balance;


}
