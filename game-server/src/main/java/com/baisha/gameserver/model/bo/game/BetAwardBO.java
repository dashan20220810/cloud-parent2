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
    private Long id;
    /**
     * 下注金额
     */
    private BigDecimal betAmount;
    /**
     * 最后派彩
     */
    private BigDecimal finalAmount;
    /**
     * 备注
     */
    private String remark;
}
