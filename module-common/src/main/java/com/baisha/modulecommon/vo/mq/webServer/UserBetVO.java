package com.baisha.modulecommon.vo.mq.webServer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 用户下注
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBetVO {

    //下注天 yyyy-MM-dd HH:mm:ss
    private String betTime;

    //用户ID
    private Long userId;

    //用户TgId
    private String tgUserId;

    //金额
    private BigDecimal amount;


}
