package com.baisha.userserver.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "会员中心-查询请求对象")
public class UserSearchVO {

    @ApiModelProperty(value = "用户名 (長度3-20,只能輸入字母,數字,_)", required = true)
    String userName;

}
