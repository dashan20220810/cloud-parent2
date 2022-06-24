package com.baisha.backendserver.model.vo.user;

import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-用户分页对象")
public class UserPageVO extends PageVO {

    @ApiModelProperty(value = "用户名(必须是6-15位的字母或数字)")
    private String userName;

}
