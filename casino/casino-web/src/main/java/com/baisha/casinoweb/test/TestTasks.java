package com.baisha.casinoweb.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.vo.mq.OpenNewGameVO;
import com.baisha.modulecommon.vo.mq.OpenVO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TestTasks {

//	@Autowired
//	private DealerBusiness dealerBusiness;
	
	private static Integer gameNo = 14;

    @Autowired
    RabbitTemplate rabbitTemplate;
	
	@Scheduled(initialDelay = 2000, fixedRate = 110000)
//	@Scheduled(initialDelay = 2000, fixedRate = 60000)  // TODO
	public void openNewGame() {

		log.info("\r\n============  游戏开局测试 ============ ");
		
//		dealerBusiness.openNewGame("127.0.0.1");
//		dealerBusiness.openNewGame("192.168.26.23");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN_NEW_GAME, JSONObject.toJSONString(OpenNewGameVO.builder().dealerIp("127.0.0.1").gameNo(gameNo).build())  );
        rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN_NEW_GAME, JSONObject.toJSONString(OpenNewGameVO.builder().dealerIp("192.168.26.23").gameNo(gameNo).build())  );
        gameNo++;
//	}
//
//	@Scheduled(initialDelay = 85000, fixedRate = 110000)
////	@Scheduled(initialDelay = 40000, fixedRate = 60000)  // TODO
//	public void open() {

        try {
			Thread.sleep(75000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
		log.info("\r\n============  开牌测试 ============ ");
//		asyncCommandService.betting("G01", "G01202206300356");
		
		List<BetOption> betOptList = new ArrayList<>(BetOption.getList());
		betOptList.remove(BetOption.D);
		betOptList.remove(BetOption.SB);
		Random rand = new Random();
		int n = rand.nextInt(betOptList.size());
		BetOption option = betOptList.get(n);
		
//		dealerBusiness.open("127.0.0.1", option.toString());
//		dealerBusiness.open("192.168.26.23", option.toString());

        rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN
        		, OpenVO.builder().dealerIp("127.0.0.1").awardOption(option.toString()).build());
        rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN
        		, OpenVO.builder().dealerIp("192.168.26.23").awardOption(option.toString()).build());
	}

//	@Scheduled(initialDelay = 88000, fixedDelay = 180000)
//	@Scheduled(initialDelay = 37000, fixedDelay = 60000)  // TODO
	public void settlement() {

		log.info("结算测试");
		
//        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, SettleFinishVO.builder().noActive("G01202206301004").build());
//        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, "192.168.26.23");
	}
}
