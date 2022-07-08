package com.baisha.gameserver;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.vo.BetReturnAmountVO;
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
	
	@Scheduled(cron = "0 10 0 * * ?", zone="Asia/Shanghai")
	public void returnAmount () {
		log.info("\r\n ========= 计算每日返水 ");
		List<BetReturnAmountVO> list = betService.returnAmountByDay(gameReturnAmountMultiplier);
		Integer dateInt = Integer.parseInt(DateUtil.dateToyyyyMMdd(DateUtils.addDays(new Date(), -1)));
		
		log.info(" 计算笔数: {} ", list.size());
		list.parallelStream().forEach( vo -> {
			betStatisticsService.updateReturnAmount(vo.getUserId(), vo.getTgChatId(), dateInt, vo.getTotalReturnAmount());
		});
	}

}
