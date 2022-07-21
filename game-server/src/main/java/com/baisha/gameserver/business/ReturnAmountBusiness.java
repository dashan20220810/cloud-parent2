package com.baisha.gameserver.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.util.enums.RedisPropEnum;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReturnAmountBusiness {

    @Value("${project.game.return-amount-multiplier}")
    private BigDecimal gameReturnAmountMultiplier;

    @Autowired
    private RedisUtil redisUtil;
    
	@Autowired
	BetService betService;
	
	@Autowired
	BetStatisticsService betStatisticsService;
	
	@Autowired
	AssetsBusiness assetsBusiness;

	public void returnAmountTask() {
		Integer queryAmount = 500;
		List<Bet> processList = betService.queryBetIsNotReturnedYesterday(queryAmount);
		Integer dateInt = Integer.parseInt(DateUtil.dateToyyyyMMdd(DateUtils.addDays(new Date(), -1)));
		Long successCount = 0L;
		Long totalCount = 0L;
        BigDecimal returnAmountMultiplier = redisUtil.getValue(RedisPropEnum.ReturnAmountMultiplier.getKey());
        if (returnAmountMultiplier == null) {
        	returnAmountMultiplier = gameReturnAmountMultiplier;
        	redisUtil.setValue(RedisPropEnum.ReturnAmountMultiplier.getKey(), returnAmountMultiplier);
        }
        final BigDecimal finalReturnAmountMultiplier = returnAmountMultiplier;
		
		while ( processList!=null & processList.size()>0 ) {
	        
			successCount += processList.stream()
				.mapToLong(bet -> doBetReturnAmoun(bet, dateInt, finalReturnAmountMultiplier)).sum();
			totalCount += processList.size();
			log.info("\r\n ==== 每日返水已下分处理笔数 {}", successCount);
			log.info("\r\n ==== 每日返水已处理笔数 {}", totalCount);
			processList = betService.queryBetIsNotReturnedYesterday(queryAmount);
		}
	}

    private Long doBetReturnAmoun(Bet bet, Integer dateInt, BigDecimal returnAmountMultiplier) {
		BigDecimal returnAmount = returnAmountMultiplier.multiply(BigDecimal.valueOf(bet.getFlowAmount())).abs();
		betStatisticsService.updateReturnAmount(bet.getUserId(), bet.getTgChatId(), dateInt, returnAmount);
		betService.updateReturnAmount(bet.getId(), returnAmount);
		
		String result = assetsBusiness.returnAmount(bet.getUserId(), returnAmount, bet.getId());
		if ( StringUtils.isNotBlank(result) ) {
			log.warn(" 呼叫用户中心-返水api 失败. user id: {}, amount: {}, bet id: {}, api result: {} "
					, bet.getUserId(), returnAmount, bet.getId(), result);
			return 0L;
		}
    	return 1L;
    }
}
