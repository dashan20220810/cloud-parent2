package com.baisha.backendserver.model.bo.user;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-会员分页对象")
public class UserPageBO extends BaseBO {

    @ApiModelProperty(value = "会员账号")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "TG用户ID")
    private String tgUserId;

    @ApiModelProperty(value = "TG群名称")
    private String tgGroupName;

    @ApiModelProperty(value = "TG群ID")
    private String tgGroupId;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;

    @ApiModelProperty(value = "来源")
    private String origin;

    @ApiModelProperty(value = "邀请码")
    private String inviteCode;

    @ApiModelProperty(value = "邀请人会员ID")
    private Long inviteUserId;

    @ApiModelProperty(value = "余额")
    private BigDecimal balance = BigDecimal.ZERO;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "打码量")
    private BigDecimal playMoney = BigDecimal.ZERO;

    @ApiModelProperty(value = "邀请人TG用户ID")
    private String inviteTgUserId;

    @ApiModelProperty(value = "邀请人TG用户名")
    private String inviteTgUserName;

}
