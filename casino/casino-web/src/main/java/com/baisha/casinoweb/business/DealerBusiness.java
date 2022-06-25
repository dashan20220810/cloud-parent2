package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.casinoweb.util.enums.TgImageEnum;
import com.baisha.core.service.TelegramService;
import com.baisha.core.vo.response.LimitStakesVO;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

@Component
public class DealerBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;

    @Autowired
    private TelegramService telegramService;
    
    @Autowired
    private GamblingBusiness gamblingBusiness;

    public boolean openNewGame ( Long tgChatId ) {

    	Map<Object, Object> sysTgMap = telegramService.getTelegramSet();
    	String openNewGameUrl = (String) sysTgMap.get(TgImageEnum.OpenNewGame.getKey());
    	LimitStakesVO limitStakesVO = telegramService.getLimitStakes(String.valueOf(tgChatId));
    	String currentActive = gamblingBusiness.currentActive(tgChatId);
    	
		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		params.put("bureauNum", currentActive);
		params.put("chatId", tgChatId);
		params.put("imageAddress", openNewGameUrl);
		params.put("minAmount", limitStakesVO.getMinAmount());
		params.put("maxAmount", limitStakesVO.getMaxAmount());
		params.put("maxShoeAmount", limitStakesVO.getMaxShoeAmount());

		String result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
            return false;
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		Integer code = betJson.getInteger("code");

		if ( code!=0 ) {
            return false;
		}
		
		return true;
    }
    
}
