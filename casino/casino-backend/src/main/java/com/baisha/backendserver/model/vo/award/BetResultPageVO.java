package com.baisha.backendserver.model.vo.award;

import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-开奖结果分页列表请求对象")
public class BetResultPageVO extends PageVO {

    @ApiModelProperty(value = "桌台ID")
    private Long tableId;

    @ApiModelProperty(value = "游戏局号")
    private String noActive;


}
