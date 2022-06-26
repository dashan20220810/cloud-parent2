package com.baisha.userserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "会员中心-新增TG会员")
public class UserAddTelegramVO {

    @ApiModelProperty(value = "TG用户ID", required = true)
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID", required = true)
    private String tgGroupId;
    
    @ApiModelProperty(value = "昵称(长度3-10位,只能输入字母或数字或汉字)", required = true)
    private String nickName;

    @ApiModelProperty(value = "密碼(长度3-20位)")
    private String password;

    @ApiModelProperty(value = "ip(长度3-128位)")
    private String ip;


}