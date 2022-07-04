package com.baisha.userserver.model.vo.balance;

import com.baisha.userserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户-打码量改变记录分页请求对象")
public class UserChangePlayMoneyPageVO extends PageVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "类型  1充值 2结算")
    private Integer changeType;


}
