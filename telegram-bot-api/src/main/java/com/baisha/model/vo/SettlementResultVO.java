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
@ApiModel(value = "SettlementResultVO对象", description = "TG群结算信息")
public class SettlementResultVO {

    @ApiModelProperty(value = "前20位输赢玩家")
    private List<UserWinVO> top20WinUsers = Lists.newArrayList();
}
