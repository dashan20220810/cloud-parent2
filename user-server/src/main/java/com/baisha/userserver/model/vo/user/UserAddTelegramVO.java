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

    @ApiModelProperty(value = "TG用户名称(@xxx)")
    private String tgUserName;

    @ApiModelProperty(value = "密碼(长度3-20位)")
    private String password;

    @ApiModelProperty(value = "ip(长度3-128位)")
    private String ip;

    @ApiModelProperty(value = "邀请人 TG用户ID")
    private String inviteTgUserId;

    @ApiModelProperty(value = "TG群名称")
    private String tgGroupName;

    @ApiModelProperty(value = "用户类型 1正式 2测试 3机器人")
    private Integer userType = 1;

    @ApiModelProperty(value = "渠道")
    private String channelCode;

    @ApiModelProperty(value = "手机号")
    private String phone;


}
