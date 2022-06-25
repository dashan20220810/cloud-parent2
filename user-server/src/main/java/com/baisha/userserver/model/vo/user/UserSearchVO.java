package com.baisha.userserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "会员中心-查询请求对象")
public class UserSearchVO {

    @ApiModelProperty(value = "用户名 (長度3-20,只能輸入字母,數字,_)(非TG用户必传)")
    String userName;

    @ApiModelProperty(value = "TG用户ID(TG用户查询必传)")
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID(TG用户查询必传)")
    private String tgGroupId;

}
