package com.baisha.gameserver.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.util.enums.RequestPathEnum;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.BalanceTypeEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AssetsBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;

	
	public String returnAmount (Long userId, BigDecimal amount, Long relatedBetId ) {
		String action = "呼叫返水api";
		Integer balanceChange = BalanceChangeEnum.RETURN_AMOUNT.getCode();
		String remarkComment = "返水"; 
		
		return assets(userId, amount, relatedBetId, action, balanceChange, BalanceTypeEnum.INCOME.getCode(), remarkComment);
	}
	
	private String assets ( Long userId, BigDecimal amount, Long relatedBetId, String action, Integer balanceChange
			, Integer balanceType, String remarkComment ) {

		String remark = String.format("user:%d ,%s", userId, remarkComment);

		log.info("{}, userID:{}", action, userId);
    	//	会员管理-下分api
    	Map<String, Object> params = new HashMap<>();
    	params.put("userId", userId);
    	params.put("amount", amount);
    	params.put("balanceType", balanceType);
    	params.put("changeType", balanceChange);
    	params.put("relateId", relatedBetId);
    	params.put("remark", remark);

    	String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.ASSETS_BALANCE.getApiName(),
				params);
		
        if (CommonUtil.checkNull(result)) {
    		log.warn("{}api 无返回资料", action);
            return "failed";
        }

		JSONObject balanceJson = JSONObject.parseObject(result);
		Integer code = balanceJson.getInteger("code");

		if ( code!=0 ) {
    		log.warn("{}api失败: {}", action, balanceJson.getString("msg"));
            return balanceJson.getString("msg");
		}

        return null;
	}
	
}
