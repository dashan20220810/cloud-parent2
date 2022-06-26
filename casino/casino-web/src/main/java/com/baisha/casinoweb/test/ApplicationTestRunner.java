package com.baisha.casinoweb.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.business.DealerBusiness;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: Alvin
 */
@Slf4j
@Component
public class ApplicationTestRunner implements ApplicationRunner {
	
	@Autowired
	private DealerBusiness dealerBusiness;

    @Override
    public void run(ApplicationArguments args) {
    	
    	while (true) {
    		log.info("游戏开局测试");
    		dealerBusiness.openNewGame((long) -795009160);
    		try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
}
