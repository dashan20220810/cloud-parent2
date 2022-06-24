package com.baisha.backendserver.model.vo.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yihui
 */
@Data
@Builder
@AllArgsConstructor
@ApiModel(value = "后台-登陆请求对象")
public class LoginVO {

    @ApiModelProperty(value = "用户名(必须是6-15位的字母或数字)", required = true)
    private String userName;

    @ApiModelProperty(value = "密码(长度限制1~20位,并且只能输入中英文)", required = true)
    private String password;

}
