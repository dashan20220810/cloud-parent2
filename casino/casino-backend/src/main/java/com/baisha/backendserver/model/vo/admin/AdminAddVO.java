package com.baisha.backendserver.model.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-新增管理员请求对象")
public class AdminAddVO {

    @ApiModelProperty(value = "账号名称，用户名(必须是6-15位的字母或数字)", required = true)
    private String userName;

    @ApiModelProperty(value = "账号持有人，昵称(长度限制1~20位,并且只能输入中英文)")
    private String nickName;

    @ApiModelProperty(value = "密码(长度限制6~15位,并且必须是数字和字母的组合)", required = true)
    private String password;

    @ApiModelProperty(value = "长度限制6~20位,并且仅允许输入数字")
    private String phone;

    @ApiModelProperty(value = "备注信息 (长度限制200)")
    private String description;

    @ApiModelProperty(value = "IP白名单 (长度限制300, 必须是数字與.號組合，符合ip規範)", required = true)
    private String allowIps;

    @ApiModelProperty(value = "员工编号 (长度限制10)")
    private String staffNo;

    @ApiModelProperty(value = "角色权限 (长度20)")
    private String role;

    @ApiModelProperty(value = "状态 1 启用（默认） ，0停用'", required = true)
    private Integer status;

    @ApiModelProperty(value = "google验证碼", required = true)
    private Integer authCode;

}
