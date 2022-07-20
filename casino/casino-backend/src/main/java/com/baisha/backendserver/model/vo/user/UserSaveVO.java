package com.baisha.backendserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-新增TG会员请求对象")
public class UserSaveVO {

    @ApiModelProperty(value = "用户名(长度3-30位,只能输入字母或数字或_)")
    private String userName;

    @ApiModelProperty(value = "昵称(长度3-20位,只能输入字母或数字或汉字)")
    private String nickName;

    @ApiModelProperty(value = "TG用户ID")
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID")
    private String tgGroupId;

    @ApiModelProperty(value = "TG群名称")
    private String tgGroupName;

    @ApiModelProperty(value = "用户类型 1正式 2测试 3机器人")
    private Integer userType;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "来源")
    private String origin;

}
