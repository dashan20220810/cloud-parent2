package com.baisha.backendserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-用户类型请求对象")
public class UserTypeVO {

    @ApiModelProperty(value = "会员账号")
    String userName;

    @ApiModelProperty(value = "用户类型")
    Integer userType;


}
