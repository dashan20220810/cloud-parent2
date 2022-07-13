package com.baisha.userserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户中心-tgId查询请求对象")
public class UserTgIdVO {

    @ApiModelProperty(value = "TG用户ID", required = true)
    private String tgUserId;

}
