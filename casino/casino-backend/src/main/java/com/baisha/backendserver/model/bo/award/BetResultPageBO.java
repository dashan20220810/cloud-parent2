package com.baisha.backendserver.model.bo.award;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BetResultPageBO extends BaseBO {

    @ApiModelProperty(value = "桌台ID")
    private Long tableId;

    @ApiModelProperty(value = "桌台名称")
    private String tableName;

    @ApiModelProperty(value = "游戏局号")
    private String noActive;

    @ApiModelProperty(value = "开奖结果类型: ZD庄对,XD闲对,Z庄,X闲,H和,(SS2,SS3)幸运六")// 可组合 英文,隔开
    private String awardOption;

    @ApiModelProperty(value = "开奖结果名称")
    private String awardOptionName;


}
