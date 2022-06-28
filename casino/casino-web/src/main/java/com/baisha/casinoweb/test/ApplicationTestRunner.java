package com.baisha.casinoweb.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.business.DealerBusiness;
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
    		
    		try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
}
