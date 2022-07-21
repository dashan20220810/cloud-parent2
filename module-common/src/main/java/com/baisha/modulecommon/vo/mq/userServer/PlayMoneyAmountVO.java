package com.baisha.modulecommon.vo.mq.userServer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlayMoneyAmountVO {

    /**
     * 注单ID
     */
    private Long betId;

    /**
     * 注单局号
     */
    private String noActive;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 打码量金额
     */
    private BigDecimal playMoney;

    /**
     * 备注
     */
    private String remark;

    /**
     * 打码量改变类型
     */
    private Integer changeType;


}
