package com.baisha.backendserver.vo.user;

import com.baisha.backendserver.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "后台-用户分页对象")
public class UserPageVO extends PageVO {

    @ApiModelProperty(value = "用户名(必须是6-15位的字母或数字)", required = true)
    private String userName;

}
