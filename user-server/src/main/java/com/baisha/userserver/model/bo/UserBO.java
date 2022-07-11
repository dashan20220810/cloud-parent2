package com.baisha.userserver.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yihui
 */
@Data
@Builder
@ApiModel(value = "注册返回对象")
public class UserBO implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;

}
