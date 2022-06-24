package com.baisha.backendserver.model.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-新增管理员请求对象")
public class AdminAddVO {

    @ApiModelProperty(value = "用户名(必须是6-15位的字母或数字)", required = true)
    private String userName;

    @ApiModelProperty(value = "昵称(长度限制1~20位,并且只能输入中英文)", required = true)
    private String nickName;

    @ApiModelProperty(value = "密码(长度限制6~15位,并且必须是数字和字母的组合)", required = true)
    private String password;

    @ApiModelProperty(value = "手机号(长度限制1~20位,并且必须是数字或字母)", required = true)
    private String phone;

}
