package com.baisha.gameserver;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baisha.gameserver.business.AssetsBusiness;
import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.modulecommon.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReturnAmountTask {

    @Value("${project.game.return-amount-multiplier}")
    private BigDecimal gameReturnAmountMultiplier;
    
	@Autowired
	BetService betService;
	
	@Autowired
	BetStatisticsService betStatisticsService;
	
	@Autowired
	AssetsBusiness assetsBusiness;
	
	@Scheduled(cron = "0 10 0 * * ?", zone="Asia/Shanghai")
	public void returnAmount () {
		log.info("\r\n ========= 计算每日返水 ");
		
		Integer queryAmount = 5000;
		List<Bet> processList = betService.queryBetIsNotReturnedYesterday(queryAmount);
		Integer dateInt = Integer.parseInt(DateUtil.dateToyyyyMMdd(DateUtils.addDays(new Date(), -1)));
		
		while ( processList!=null & processList.size()>0 ) {
			
			processList.parallelStream().map(bet -> {
				BigDecimal returnAmount = gameReturnAmountMultiplier.multiply(bet.getWinAmount()).abs();
				betStatisticsService.updateReturnAmount(bet.getUserId(), bet.getTgChatId(), dateInt, returnAmount);
				
				String result = assetsBusiness.returnAmount(bet.getUserId(), returnAmount, bet.getId());
				if ( StringUtils.isNotBlank(result) ) {
					log.warn(" 呼叫用户中心-返水api 失败. user id: {}, amount: {}, bet id: {}, api result: {} "
							, bet.getUserId(), returnAmount, bet.getId(), result);
				}
				return result;
			});
		}
	}

}
