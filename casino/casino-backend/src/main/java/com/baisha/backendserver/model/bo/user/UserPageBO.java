package com.baisha.backendserver.model.bo.user;

import com.baisha.backendserver.model.bo.BaseBO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "用户类型名称")
    private String userTypeName;

    @ApiModelProperty(value = "渠道")
    private String channelCode;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "注单数")
    private Integer betNum = 0;

    @ApiModelProperty(value = "累计投注额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计盈亏")
    private BigDecimal winAmount = BigDecimal.ZERO;

    @ApiModelProperty(value = "最后投注时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastBetTime;

}
