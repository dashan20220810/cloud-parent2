package com.baisha.casinoweb.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.AsyncCommandService;
import com.baisha.casinoweb.business.DealerBusiness;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestTasks {

	@Autowired
	private DealerBusiness dealerBusiness;
	
	@Autowired
	private AsyncCommandService asyncCommandService;
	
//	@Scheduled(fixedRate = 1800000)
	@Scheduled(initialDelay = 2000, fixedDelay = 15000)
	public void openNewGame() {

		log.info("游戏开局测试");
//		dealerBusiness.openNewGame("127.0.0.1");
		
		asyncCommandService.betting("G01", "G01202206300356");
//		dealerBusiness.openNewGame("192.168.26.23");
	}
	
}
