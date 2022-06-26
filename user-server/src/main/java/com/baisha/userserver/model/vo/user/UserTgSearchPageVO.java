package com.baisha.userserver.model.vo.user;

import com.baisha.userserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "会员中心-查询群下会员请求对象")
public class UserTgSearchPageVO extends PageVO {

    @ApiModelProperty(value = "TG群ID", required = true)
    private String tgGroupId;

    @ApiModelProperty(value = "会员")
    private String userName;

}
