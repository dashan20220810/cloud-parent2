package com.baisha.backendserver.model.vo.user;

import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-打码量改变记录分页请求对象")
public class UserChangePlayMoneyPageVO extends PageVO {

    @ApiModelProperty(value = "用户ID", required = true)
    private Long id;

    @ApiModelProperty(value = "变动类型  1充值 2结算")
    private Integer changeType;

    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss")
    private String endTime;


}
