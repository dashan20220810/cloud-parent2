package com.baisha.casinoweb.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@ApiModel(value = "web-用户中心-余额返回对象")
public class BalanceBO {

    @ApiModelProperty(value = "余额")
    private String balance;

}