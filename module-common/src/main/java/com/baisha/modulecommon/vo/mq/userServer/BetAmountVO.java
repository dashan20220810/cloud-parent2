package com.baisha.modulecommon.vo.mq.userServer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BetAmountVO {

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
     * 派彩金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 余额改变类型
     */
    private Integer changeType;


}
