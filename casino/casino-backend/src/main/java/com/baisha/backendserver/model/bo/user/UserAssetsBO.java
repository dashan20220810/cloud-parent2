package com.baisha.backendserver.model.bo.user;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
public class UserAssetsBO extends BaseBO {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "会员ID")
    private Long userId;

    @ApiModelProperty(value = "TG用户ID")
    private String tgUserId;

    @ApiModelProperty(value = "余额")
    private BigDecimal balance;

    //@ApiModelProperty(value = "冻结余额")
    //private BigDecimal freezeAmount;

    @ApiModelProperty(value = "打码量")
    private BigDecimal playMoney;


}
