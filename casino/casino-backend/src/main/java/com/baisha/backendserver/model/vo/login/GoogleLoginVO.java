package com.baisha.backendserver.model.vo.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value = "后台-登陆返回对象")
public class GoogleLoginVO {

    @ApiModelProperty(value = "ID",required = true)
    private Long id;

    @ApiModelProperty(value = "google auth code", required = true)
    private Integer googleAuthCode;

}
