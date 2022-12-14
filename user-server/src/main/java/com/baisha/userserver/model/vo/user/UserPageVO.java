package com.baisha.userserver.model.vo.user;

import com.baisha.userserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "会员中心-用户分页对象")
public class UserPageVO extends PageVO {

    @ApiModelProperty(value = "用户名")
    String userName;

    @ApiModelProperty(value = "昵称")
    String nickName;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    private String endTime;

}
