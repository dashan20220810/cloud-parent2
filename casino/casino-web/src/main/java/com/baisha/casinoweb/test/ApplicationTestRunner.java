package com.baisha.casinoweb.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.business.DealerBusiness;
import com.baisha.casinoweb.business.GamblingBusiness;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: Alvin
 */
@Slf4j
@Component
public class ApplicationTestRunner implements ApplicationRunner {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
	
	@Autowired
	private DealerBusiness dealerBusiness;
    
    @Autowired
    private GamblingBusiness gamblingBusiness;

    @Override
    public void run(ApplicationArguments args) {
    	
    	while (true) {
    		log.info("游戏开局测试");
//    		dealerBusiness.openNewGame();

        	Map<String, Object> params = new HashMap<>();

    		String result = HttpClient4Util.doPost(
    				"http://127.0.0.1:9401/dealer/openNewGame",
    				params);
    		log.info(result.toString());

        	String currentActive = gamblingBusiness.generateNewActive("G02");
        	String openNewGameUrl = "http://192.168.26.24:9000/user/open_new_game.jpg";
    		// 记录IP
        	params = new HashMap<>();
    		params.put("bureauNum", currentActive);
    		params.put("tableId", 2);
    		params.put("imageAddress", openNewGameUrl);

    		log.info("局号、桌台id、新局图片url: {}, {}, {}", currentActive, 2, openNewGameUrl);
    		result = HttpClient4Util.doPost(
    				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
    				params);

    		
    		try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
}
