package com.baisha.userserver.model.vo.balance;

import com.baisha.userserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "用户-余额改变记录分页请求对象")
public class UserChangeBalancePageVO extends PageVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;


    @ApiModelProperty(value = "类型  1充值 2下注 3 派奖")
    private Integer changeType;


}
