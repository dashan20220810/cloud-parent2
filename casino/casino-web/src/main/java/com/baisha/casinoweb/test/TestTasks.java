package com.baisha.casinoweb.test;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.AsyncCommandService;
import com.baisha.casinoweb.business.DealerBusiness;
import com.baisha.modulecommon.enums.BetOption;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestTasks {

	@Autowired
	private DealerBusiness dealerBusiness;
	
	@Autowired
	private AsyncCommandService asyncCommandService;
	
	@Scheduled(initialDelay = 2000, fixedRate = 180000)
//	@Scheduled(initialDelay = 2000, fixedDelay = 15000)  // TODO
	public void openNewGame() {

		log.info("游戏开局测试");
//		asyncCommandService.betting("G01", "G01202206300356");
		
		dealerBusiness.openNewGame("127.0.0.1");
		dealerBusiness.openNewGame("192.168.26.23");
	}

	@Scheduled(initialDelay = 85000, fixedRate = 180000)
//	@Scheduled(initialDelay = 85000, fixedDelay = 15000)  // TODO
	public void open() {

		log.info("开牌测试");
//		asyncCommandService.betting("G01", "G01202206300356");
		
		List<BetOption> betOptList = BetOption.getList();
		Random rand = new Random();
		int n = rand.nextInt(betOptList.size());
		BetOption option = betOptList.get(n);
		
		dealerBusiness.open("127.0.0.1", option.toString());
		dealerBusiness.open("192.168.26.23", option.toString());
	}
}
