package com.baisha.backendserver.model.bo.user;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    @ApiModelProperty(value = "TG群ID")
    private String tgGroupId;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status = 1;

    @ApiModelProperty(value = "来源")
    private String origin;
}
