package com.baisha.backendserver.vo.admin;

import com.baisha.backendserver.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-管理员分页请求对象")
public class AdminPageVO extends PageVO {

    @ApiModelProperty(value = "用户名(必须是6-15位的字母或数字)")
    String userName;

}
