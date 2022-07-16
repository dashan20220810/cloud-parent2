package com.baisha.casinoweb.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.model.bo.BalanceBO;
import com.baisha.casinoweb.model.vo.UserVO;
import com.baisha.casinoweb.model.vo.response.DeskVO;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.BalanceTypeEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AssetsBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;

	@Autowired
	private GameInfoBusiness gameInfoBusiness;
	
	@Autowired
	private DeskBusiness deskBusiness;
	
	@Autowired
	private UserBusiness userBusiness;
	
	public String withdraw ( Long userId, Long amount, Long tableId, Long relatedBetId ) {
		String action = "呼叫下分api";
		Integer balanceChange = BalanceChangeEnum.BET.getCode();
		String remarkComment = "下注"; 
		
		return assets(userId, new BigDecimal(amount), tableId, relatedBetId, action, balanceChange, BalanceTypeEnum.EXPENSES.getCode(), remarkComment);
	}
	
	public String returnAmount (Long userId, BigDecimal amount, Long relatedBetId ) {
		String action = "呼叫返水api";
		Integer balanceChange = BalanceChangeEnum.RETURN_AMOUNT.getCode();
		String remarkComment = "返水"; 
		
		return assets(userId, amount, null, relatedBetId, action, balanceChange, BalanceTypeEnum.INCOME.getCode(), remarkComment);
	}
	
	private String assets ( Long userId, BigDecimal amount, Long tableId, Long relatedBetId, String action, Integer balanceChange
			, Integer balanceType, String remarkComment ) {

		String remark = "";
		
		if ( tableId!=null ) {
			DeskVO desk = deskBusiness.queryDeskById(tableId);
			String deskCode = desk.getDeskCode();
			GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
			remark = String.format("user:%d ,局号:%s %s", userId, gameInfo.getCurrentActive(), remarkComment);
		} else {
			remark = String.format("user:%d ,%s", userId, remarkComment);
		}

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
	
	
	public BigDecimal balance () {
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	Long userId = null;
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	
    	if ( isTgRequest ) {
    		UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    		if ( userVO==null ) {
    			return null;
    		}
    		userId = userVO.getId();
    	} else {
    		userId = Long.parseLong(userIdOrName);
    	}

    	String result = HttpClient4Util.doGet(
				userServerDomain + RequestPathEnum.ASSETS_QUERY.getApiName() +"?userId=" + userId);
        if (CommonUtil.checkNull(result)) {
            return null;
        }

		JSONObject balanceJson = JSONObject.parseObject(result);
		Integer code = balanceJson.getInteger("code");
		if ( code!=0 ) {
            return null;
		}
		
		return new BigDecimal(JSONObject.parseObject(balanceJson.getString("data"), BalanceBO.class).getBalance());
	}
}
