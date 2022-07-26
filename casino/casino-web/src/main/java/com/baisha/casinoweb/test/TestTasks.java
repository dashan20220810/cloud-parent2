package com.baisha.casinoweb.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.baisha.casinoweb.business.GamblingBusiness;
import com.baisha.casinoweb.business.GameInfoBusiness;
import com.baisha.casinoweb.model.OpenCardVideo;
import com.baisha.modulecommon.vo.mq.PairImageVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.apache.commons.lang3.time.DateUtils;
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
	
	private static Integer gameNo = 10;

    @Autowired
    RabbitTemplate rabbitTemplate;

	@Autowired
	private GamblingBusiness gamblingBusiness;

	
//	@Scheduled(initialDelay = 2000, fixedRate = 110000)
//	@Scheduled(initialDelay = 2000, fixedRate = 30000)  // TODO
	public void openNewGame() {

		log.info("\r\n============  游戏开局测试 ============ ");
		
//		dealerBusiness.openNewGame("127.0.0.1");
//		dealerBusiness.openNewGame("192.168.26.23");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String newActive = gamblingBusiness.generateNewActive("G01", gameNo);
        rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN_NEW_GAME, JSONObject
				.toJSONString(OpenNewGameVO.builder().dealerIp("127.0.0.1")
						.gameNo(newActive).startTime(DateUtils.addSeconds(new Date(), -10)).bootsNo("1")
						.countDown(70).build())  );
//        rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN_NEW_GAME, JSONObject.toJSONString(OpenNewGameVO.builder().dealerIp("192.168.26.23").gameNo(gameNo).build())  );
//		rabbitTemplate.convertAndSend(MqConstants.WEB_OPEN_NEW_GAME, JSONObject.toJSONString(OpenNewGameVO.builder().dealerIp("127.0.0.1").gameNo(gameNo++).build())  );
//	}
//
//	@Scheduled(initialDelay = 85000, fixedRate = 110000)
//	@Scheduled(initialDelay = 40000, fixedRate = 60000)  // TODO
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
		File file = new File("/Users/developer/Desktop/Snipaste_2022-07-16_13-16-46.jpeg");
		byte[] data = new byte[0];
		try {
			FileInputStream fis =new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int num;
			while ((num = fis.read(b)) != -1) {
				bos.write(b, 0, num);
			}
			fis.close();
			data = bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
        rabbitTemplate.convertAndSend(MqConstants.WEB_CLOSE_GAME
        		, JSONObject.toJSONString(OpenVO.builder().dealerIp("127.0.0.1").gameNo(newActive)
						.consequences(String.valueOf(option.getOrder()))
						.endTime("2022-07-13 16:34:00").build()));
//        rabbitTemplate.convertAndSend(MqConstants.WEB_CLOSE_GAME
//        		, OpenVO.builder().dealerIp("192.168.26.23").consequences(option.toString()).build());


		log.info("\r\n============  截屏测试 ============ ");
//		rabbitTemplate.convertAndSend(MqConstants.WEB_PAIR_IMAGE
//				, JSONObject.toJSONString(PairImageVO.builder().dealerIp("127.0.0.1")
//						.imageContent(data)
//						.gameNo(newActive).build()));

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.info("\r\n============  结算测试 ============ ");
		rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH
				, JSONObject.toJSONString(SettleFinishVO.builder().dealerIp("127.0.0.1")
						.consequences("146")
						.gameNo(newActive).build()));

		gameNo++;
	}

//	@Scheduled(initialDelay = 88000, fixedDelay = 180000)
//	@Scheduled(initialDelay = 37000, fixedDelay = 60000)  // TODO
	public void settlement() {

		log.info("结算测试");
		
//        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, SettleFinishVO.builder().noActive("G01202206301004").build());
//        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, "192.168.26.23");
	}
}
