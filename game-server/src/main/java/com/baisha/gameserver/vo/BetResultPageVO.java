package com.baisha.gameserver.vo;

import com.baisha.modulecommon.PageVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "web-开牌结果分页对象")
public class BetResultPageVO extends PageVO {

    @ApiModelProperty("user_id")
    private Long userId;
    
    @ApiModelProperty("tg_chat_id")
    private Long tgChatId;

    @ApiModelProperty("统计日期")
    private Integer statisticsDate;
}
