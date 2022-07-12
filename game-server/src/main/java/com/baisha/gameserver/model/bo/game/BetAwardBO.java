package com.baisha.gameserver.model.bo.game;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 注单  单个中奖选择
 *
 * @author yihui
 */
@Data
public class BetAwardBO {

    /**
     * 下注金额
     */
    private BigDecimal betAmount;
    /**
     * 最后派彩
     */
    private BigDecimal finalAmount;
    /**
     * 输赢金额
     */
    //private BigDecimal winAmount;

    /**
     * 备注
     */
    private String remark;
}
