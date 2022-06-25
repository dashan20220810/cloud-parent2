package com.baisha.backendserver.model.bo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@Builder
@ApiModel(value = "后台-登陆返回对象")
public class LoginBO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "账号")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "token")
    private String token;
}
