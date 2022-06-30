package com.baisha.model.vo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "BetUserAmountVO对象", description = "TG群下注信息")
public class BetUserAmountVO {

    @ApiModelProperty(value = "总下注金额", required = true)
    private String totalBetAmount;

    @ApiModelProperty(value = "前20位下注玩家")
    private List<BetUserVO> top20Users= Lists.newArrayList();
}
