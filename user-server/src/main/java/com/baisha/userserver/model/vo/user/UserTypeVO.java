package com.baisha.userserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户-会员类型请求对象")
public class UserTypeVO {

    @ApiModelProperty(value = "会员账号")
    String userName;

    @ApiModelProperty(value = "用户类型")
    Integer userType;

}
