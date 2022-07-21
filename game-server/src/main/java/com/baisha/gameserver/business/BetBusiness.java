package com.baisha.gameserver.business;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.util.enums.RedisPropEnum;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author alvin
 */
@Slf4j
@Service
public class BetBusiness {

    @Value("${project.game.return-amount-multiplier}")
    private BigDecimal gameReturnAmountMultiplier;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private BetService betService;

    @Autowired
    private BetStatisticsService betStatisticsService;

    @Autowired
    private AssetsBusiness assetsBusiness;


    /**
     * 修正结算表的返水
     *
     * @param bet
     */
    @Async
    public void fixReturnAmount(Bet bet) {
        betStatisticsService.updateReturnAmount(bet.getUserId(), bet.getTgChatId(),
                Integer.parseInt(DateUtil.dateToyyyyMMdd(bet.getCreateTime())), BigDecimal.ZERO.subtract(bet.getReturnAmount()));
    }

    /**
     * 重新计算注单及结算返水
     *
     * @param bet
     * @return
     */
    public BigDecimal updateReturnAmount(Bet bet) {
        if (bet.getWinAmount().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal returnAmount;

            BigDecimal returnAmountMultiplier = redisUtil.getValue(RedisPropEnum.ReturnAmountMultiplier.getKey());
            if (returnAmountMultiplier == null) {
                returnAmountMultiplier = gameReturnAmountMultiplier;
                redisUtil.setValue(RedisPropEnum.ReturnAmountMultiplier.getKey(), returnAmountMultiplier);
            }

            returnAmount = returnAmountMultiplier.multiply(BigDecimal.valueOf(bet.getFlowAmount())).abs();
            betStatisticsService.updateReturnAmount(bet.getUserId(), bet.getTgChatId(),
                    Integer.parseInt(DateUtil.dateToyyyyMMdd(bet.getCreateTime())), returnAmount);
            betService.updateReturnAmount(bet.getId(), returnAmount);
            return returnAmount;
        }
        return BigDecimal.ZERO;

    }


    @Async
    public void updateReturnAmountReopen(Bet bet) {
        BigDecimal returnAmount = updateReturnAmount(bet);
        assetsBusiness.returnAmountReopen(bet.getUserId(), returnAmount, bet.getId());
    }

}
