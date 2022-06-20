package com.baisha.userserver.vo.user;

import com.baisha.userserver.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "会员中心-用户分页对象")
public class UserPageVO extends PageVO {

    @ApiModelProperty(value = "用户名 (長度3-20,只能輸入字母,數字,_)")
    String userName;

}
