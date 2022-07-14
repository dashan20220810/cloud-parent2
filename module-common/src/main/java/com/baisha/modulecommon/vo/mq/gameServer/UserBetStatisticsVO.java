package com.baisha.modulecommon.vo.mq.gameServer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 注单结算后，告诉backend统计用户注单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBetStatisticsVO {

    //用户ID
    private Long userId;

    // 累计盈亏
    private BigDecimal winAmount;

    //下注天 yyyy-MM-dd HH:mm:ss
    private String betTime;

}
