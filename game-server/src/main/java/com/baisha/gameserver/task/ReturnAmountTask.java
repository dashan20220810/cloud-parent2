package com.baisha.gameserver.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baisha.gameserver.business.ReturnAmountBusiness;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReturnAmountTask {
	
	@Autowired
	ReturnAmountBusiness returnAmountBusiness;
	
	@Scheduled(cron = "0 10 00 * * ?", zone="Asia/Shanghai")
	public void returnAmount () {
		log.info("\r\n ========= 计算每日返水 ");
		returnAmountBusiness.returnAmountTask();
	}


}
