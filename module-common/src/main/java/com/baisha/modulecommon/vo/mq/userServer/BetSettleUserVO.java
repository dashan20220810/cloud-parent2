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
public class BetSettleUserVO {

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
     * 打码量
     */
    private BigDecimal playMoney;

    /**
     * 派彩金额
     */
    private BigDecimal finalAmount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否重新派彩
     */
    private Integer isReopen;

}
