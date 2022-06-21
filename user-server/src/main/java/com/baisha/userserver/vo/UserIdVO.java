package com.baisha.userserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户中心-用户ID请求对象")
public class UserIdVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;
}
