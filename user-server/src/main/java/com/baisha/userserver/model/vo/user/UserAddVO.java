package com.baisha.userserver.model.vo.user;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "会员中心-新增会员")
public class UserAddVO {

    @ApiModelProperty(value = "用户名(长度3-30位,只能输入字母或数字或_)")
    private String userName;

    @ApiModelProperty(value = "昵称(长度3-10位,只能输入字母或数字或汉字)")
    private String nickName;

    @ApiModelProperty(value = "密碼(长度3-20位)")
    private String password;

    @ApiModelProperty(value = "ip(长度3-128位)")
    private String ip;

    @ApiModelProperty(value = "来源")
    private String origin;

    @ApiModelProperty(value = "TG用户ID")
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID")
    private String tgGroupId;

    @ApiModelProperty(value = "邀请人 TG用户ID")
    private String inviteTgUserId;

    @ApiModelProperty(value = "TG群名称")
    private String tgGroupName;

}
