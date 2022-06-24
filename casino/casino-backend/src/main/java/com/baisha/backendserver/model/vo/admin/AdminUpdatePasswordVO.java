package com.baisha.backendserver.model.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-修改密码请求对象")
public class AdminUpdatePasswordVO {

    @ApiModelProperty(value = "旧密码(长度限制6~15位,并且必须是数字和字母的组合)", required = true)
    private String oldPassword;

    @ApiModelProperty(value = "新密码(长度限制6~15位,并且必须是数字和字母的组合)", required = true)
    private String newPassword;

}
