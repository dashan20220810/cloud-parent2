package com.baisha.backendserver.model.vo.award;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "后台-补单请求对象")
public class BetResultRepairVO {


    @ApiModelProperty(value = "类型: ZD庄对,XD闲对,Z庄,X闲,H和,(SS2,SS3)幸运六  多个英文,隔开", required = true)
    private String awardOption;

    @ApiModelProperty(value = "游戏局号", required = true)
    private String noActive;


}
