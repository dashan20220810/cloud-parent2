package com.baisha.casinoweb.business;

import java.math.BigDecimal;
import java.util.*;

import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.TgGameInfo;
import com.baisha.modulecommon.vo.mq.webServer.BsOddsVO;
import com.beust.jcommander.internal.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baisha.casinoweb.model.bo.BsOddsBO;
import com.baisha.casinoweb.model.vo.BetVO;
import com.baisha.casinoweb.model.vo.UserVO;
import com.baisha.casinoweb.model.vo.response.BetResponseVO;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.util.ValidateUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.enums.BetStatusEnum;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulecommon.util.SnowFlakeUtils;
import com.baisha.modulecommon.vo.GameTgGroupInfo;
import com.baisha.modulecommon.vo.GameUserInfo;
import com.baisha.modulecommon.vo.mq.webServer.UserBetVO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderBusiness {
	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

	@Autowired
	private GameInfoBusiness gameInfoBusiness;
	
	@Autowired
	private UserBusiness userBusiness;
	
	@Autowired
	private DeskBusiness deskBusiness;
	
	@Autowired
	private AssetsBusiness assetsBusiness;
	

	
	public String bet ( boolean isTgRequest, BetVO betVO) {
		return bet(isTgRequest, betVO.getNoActive(), betVO.getTgChatId(), betVO.getBetOptionList(), betVO.getAmount());
	}
	
	public String bet ( boolean isTgRequest, String noActive, Long tgChatId, List<String> betOptionList,
			Long amount) {
		
		String action = "??????";
		log.info(action);
		Long totalAmount = 0L;

    	// ????????????
//    	DeskVO deskVO = deskBusiness.queryDeskById(tableId);
//    	if ( deskVO==null ) {
//    		log.warn("[??????] ?????????????????????, table id: {}", tableId);
//            return "??????????????????";
//    	}
//		String deskCode = deskVO.getDeskCode();
		TgGameInfo tgGameInfo = gameInfoBusiness.getTgGameInfo(noActive);
		// ????????????
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
		// ??????s
		String noRun = gameInfo.getBootsNo();
		Date endTime = gameInfo.getEndTime();
    	
		if ( tgGameInfo==null ) {
    		log.warn("?????? ?????? ???????????????");
			return "???????????????";
		}
		
		if ( StringUtils.isBlank(noActive) ) {
    		log.warn("?????? ?????? ????????????, {}");
			return "????????????";
		}
		
		Date now = new Date();
		
		if ( tgGameInfo.getStatus()!= GameStatusEnum.Betting || (endTime!=null && now.after(endTime)) ) {
    		log.warn("?????? ?????? ???????????????, {}, ??????????????????: {}", tgGameInfo.getStatus().toString(), endTime);
            return "???????????????";
		}
    	
    	// ????????????
    	GameTgGroupInfo groupInfo = tgGameInfo.getTgGroupInfo(tgChatId);
    	/// 20220713 ?????????????????????
//    	if ( groupInfo.checkTotalBetAmount(betVO.getTotalAmount(), betVO.getMaxShoeAmount().longValue())==false ) {
//            return ResponseUtil.custom(String.format("???????????? ???????????????????????? %s", betVO.getMaxShoeAmount()));
//    	}
    	
    	//  user id???user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.warn("[??????] user????????????, id: {}", userIdOrName);
            return "??????????????????";
    	}
    	
    	if (Objects.equals(UserVO.Status.DISABLE.getCode(), userVO.getStatus())) {
    		log.warn("[??????] user????????????");
            return "??????????????????";
    	}

    	Long userId = userVO.getId();
    	String userName = userVO.getUserName();
    	String nickName = userVO.getNickName();
    	GameUserInfo userInfo = groupInfo.getUserInfo(userVO.getId());

		String result = HttpClient4Util.doGet(
				gameServerDomain + RequestPathEnum.GAME_ODDS_LIST.getApiName() +"?gameCode=BACC"); 

		if (!ValidateUtil.checkHttpResponse(action, result)) {
            return String.format("???????????? ??????, %s", StringUtils.defaultString(result));
		}

		List<BsOddsBO> limitList = JSONObject.parseObject(JSONObject.parseObject(result).getString("data"), new TypeReference<List<BsOddsBO>>(){});
    	
    	for (String betOption: betOptionList) {
			if(BetOption.Z_X.contains(betOption) && !StringUtils.isEmpty(userInfo.getBetHistoryString())){
				if(!userInfo.getBetOptionByUser(betOption)){
					return "?????????????????????";
				}
			}

    		// BsOdds??? ??????6???????????????????????????ss2
    		Optional<BsOddsBO> oddsBoOpt = limitList.stream().filter( 
    				odds -> (StringUtils.equalsIgnoreCase(betOption, odds.getRuleCode()) 
    					|| ("SS".equalsIgnoreCase(betOption) && "SS2".equalsIgnoreCase(odds.getRuleCode())))
    		).findFirst();
    		
    		BsOddsBO oddsBO = null;
    		if (oddsBoOpt.isPresent()) {
    			oddsBO = oddsBoOpt.get();
    		} else {
    			return String.format("???????????? ????????????:%s", betOption);
    		}
    		
    		if ( oddsBO.getMinAmount()==null || oddsBO.getMinAmount()==null ) {
    			return "??????????????????";
    		}
    		
        	if (!userInfo.checkUserBetAmount(betOption, amount
					, oddsBO.getMinAmount().longValue(), oddsBO.getMaxAmount().longValue())) {
                return String.format("???????????? %s %s-%s", betOption, oddsBO.getMinAmount(), oddsBO.getMaxAmount());
        	}
    	}
    	
    	gameInfoBusiness.setTgGameInfo(noActive, tgGameInfo);

		// gs??????
		Map<String, Object> params = new HashMap<>();
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());

		params.put("isTgRequest", isTgRequest);
		params.put("tgChatId", tgChatId);
		params.put("clientIP", ip);
		params.put("userId", userId);  
		params.put("userName", userName); 
		params.put("nickName", nickName); 
		params.put("noRun", noRun);
		params.put("noActive", noActive);
		params.put("status", BetStatusEnum.BET.getCode());
		params.put("orderNo", SnowFlakeUtils.getSnowId());
		
		for ( String betOption: betOptionList ) {
			if ( StringUtils.equalsIgnoreCase(betOption, BetOption.X.toString()) ) {
				params.put("amountX", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.Z.toString()) ) {
				params.put("amountZ", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.H.toString()) ) {
				params.put("amountH", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.XD.toString()) ) {
				params.put("amountXd", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.ZD.toString()) ) {
				params.put("amountZd", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.SS.toString()) ) {
				params.put("amountSs", amount);
			}
			
			totalAmount += amount;
		}

		
		result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_BET.getApiName(),
				params);

		if (!ValidateUtil.checkHttpResponse(action, result)) {
			log.warn("\r\n===== ?????? ??????, ??????api??????, ??????id:{}, ??????:{}, ????????????:{}", userId, noActive, result);
            return String.format("%s", StringUtils.defaultString(result));
		}

		JSONObject betJson = JSONObject.parseObject(result);
		Long betId = betJson.getLong("data");

    	//  ??????
    	//	???????????? - ??????api
    	String withdrawResult = assetsBusiness.withdraw(userId, totalAmount, noActive, betId);
    	if ( StringUtils.isNotBlank(withdrawResult) ) {
    		log.warn("[??????] ????????????");
    		params = new HashMap<>();
    		params.put("betId", betId);
    		
    		result = HttpClient4Util.doPost(
    				gameServerDomain + RequestPathEnum.ORDER_DELETE.getApiName(),
    				params);

    		if (!ValidateUtil.checkHttpResponse(action, result)) {
    			log.warn("\r\n===== ?????? ??????, ??????api??????, betId:{}, ??????id:{}, ??????:{}, ????????????:{}", betId.toString(), userId, noActive, result);
                return String.format("??????????????????bet, id:%s, %s", betId.toString(), StringUtils.defaultString(result));
    		}
            return withdrawResult;
    	}
    	
		tgGameInfo = gameInfoBusiness.calculateBetAmount(noActive, tgChatId, userId, nickName, betOptionList, amount);
		
		UserBetVO userBetVO = UserBetVO.builder().amount(BigDecimal.valueOf(totalAmount))
				.userId(userId).betTime(DateUtil.dateToPatten(now))
				.tgUserId(userName).build();
		rabbitTemplate.convertAndSend(MqConstants.USER_BET_STATISTICS, JSONObject.toJSONString(userBetVO));
		
		log.info("?????? ??????");
		return null;
	}
	
	public List<BetResponseVO> currentOrderList (Long tgChatId, Integer queryAmount) {
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id???user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("???????????? ?????? ?????????????????????");
            return null;
    	}

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userVO.getId()); 
        params.put("tgChatId", tgChatId);
        params.put("queryAmount", queryAmount);
        String result = HttpClient4Util.doPost(gameServerDomain + RequestPathEnum.ORDER_CURRENT_LIST.getApiName(), params);
        if (CommonUtil.checkNull(result)) {
            return null;
        }
        JSONObject json = JSONObject.parseObject(result);

		List<BetResponseVO> betResponseVOList = Optional.ofNullable(JSONObject
				.parseArray(json.getString("data"), BetResponseVO.class)).orElse(Lists.newArrayList());
		betResponseVOList.forEach(obj->{
			if(null == obj.getWinAmount()){
				obj.setWinStrAmount("-");
			}else{
				obj.setWinStrAmount(obj.getWinAmount().stripTrailingZeros().toPlainString());
			}
		});
		return betResponseVOList;
	}
	
	public BigDecimal todayTotalWater (Long tgChatId) {
		String action = "????????????";
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id???user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("{} ?????? ?????????????????????", action);
            return null;
    	}

        String result = HttpClient4Util.doGet(gameServerDomain + RequestPathEnum.ORDER_WATER.getApiName() +"?userId=" +userVO.getId() +"&tgChatId=" +tgChatId);
        
        if (!ValidateUtil.checkHttpResponse(action, result)) {
        	return null;
        }
        
        JSONObject json = JSONObject.parseObject(result);
        
        return json.getBigDecimal("data");
	}
	
	public BigDecimal todayTotalProfit (Long tgChatId) {
		String action = "????????????";
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id???user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("{} ?????? ?????????????????????", action);
            return null;
    	}

        String result = HttpClient4Util.doGet(gameServerDomain + RequestPathEnum.ORDER_PROFIT.getApiName() +"?userId=" +userVO.getId() +"&tgChatId=" +tgChatId);
        
        if (!ValidateUtil.checkHttpResponse(action, result)) {
        	return null;
        }
        
        JSONObject json = JSONObject.parseObject(result);
        
        return json.getBigDecimal("data");
	}
	
	public BigDecimal returnAmount (Long tgChatId) {
		String action = "????????????";
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id???user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("{} ?????? ?????????????????????", action);
            return null;
    	}
    	
    	// ?????????????????????
    	String result = HttpClient4Util.doGet(gameServerDomain + RequestPathEnum.ORDER_QUERY_BET_IS_NOT_RETURNED.getApiName() +"?userId=" +userVO.getId() +"&tgChatId=" +tgChatId);
        if (!ValidateUtil.checkHttpResponse(action, result)) {
        	return null;
        }

        JSONObject json = JSONObject.parseObject(result);
		List<BetResponseVO> responseList = JSONObject.parseObject(json.getString("data"), new TypeReference<List<BetResponseVO>>(){});
        BigDecimal totalReturnAmount = BigDecimal.ZERO;
        
        for ( BetResponseVO vo: responseList ) {
        	
        	// TODO ????????????
        	
        	// ????????????
            Map<String, Object> params = new HashMap<>();
            params.put("betId", vo.getId());
            params.put("userId", vo.getUserId());
            params.put("tgChatId", vo.getTgChatId());
//            params.put("winAmount", vo.getWinAmount());
            result = HttpClient4Util.doPost(gameServerDomain + RequestPathEnum.ORDER_RETURN_AMOUNT.getApiName(), params);
            if (!ValidateUtil.checkHttpResponse(action, result)) {
                log.info("\r\n==== ????????????, ??????id: {}, ??????api??????: {}", vo.getId(), result);
            	return null;
            }
            
            json = JSONObject.parseObject(result);
            BigDecimal returnAmount = json.getBigDecimal("data");
            totalReturnAmount = totalReturnAmount.add(returnAmount);
            
            // ??????(??????)
            String raResult = assetsBusiness.returnAmount(vo.getUserId(), returnAmount, vo.getId());
            if ( StringUtils.isNotBlank(raResult) ) {
                log.info("\r\n==== ????????????, ??????id: {}, ??????api??????: {}", vo.getId(), raResult);
            	return null;
            }
            log.info("????????????, ??????id: {}", vo.getId());
        }
        
        return totalReturnAmount;
	}

	public List<BsOddsVO> redLimit() {
		String result = HttpClient4Util.doGet(gameServerDomain + RequestPathEnum.GAME_ODDS_LIST.getApiName());
		JSONObject json = JSONObject.parseObject(result);
		return JSONObject.parseObject(json.getString("data"), new TypeReference<List<BsOddsVO>>(){});
	}
}
