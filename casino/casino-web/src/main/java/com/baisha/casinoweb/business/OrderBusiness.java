package com.baisha.casinoweb.business;

import java.math.BigDecimal;
import java.util.*;

import com.baisha.modulecommon.vo.mq.webServer.BsOddsVO;
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
import com.baisha.casinoweb.model.vo.response.DeskVO;
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
import com.baisha.modulecommon.vo.GameInfo;
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
	

	
	public String bet ( boolean isTgRequest, BetVO betVO, String noRun ) {
		return bet(isTgRequest, betVO.getTableId(), betVO.getTgChatId(), betVO.getBetOptionList(), betVO.getAmount()
				, noRun);
	}
	
	public String bet ( boolean isTgRequest, Long tableId, Long tgChatId, List<String> betOptionList, 
			Long amount, String noRun ) {
		
		String action = "下注";
		log.info(action);
		Long totalAmount = 0L;

    	// 桌台资料
    	DeskVO deskVO = deskBusiness.queryDeskById(tableId);
    	if ( deskVO==null ) {
    		log.warn("[下注] 桌台号查无资料, table id: {}", tableId);
            return "桌台资料错误";
    	}
		String deskCode = deskVO.getDeskCode();
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	
		if ( gameInfo==null ) {
    		log.warn("下注 失败 无游戏资讯");
			return "下注 失败 无游戏资讯";
		}
		
		if ( StringUtils.isBlank(gameInfo.getCurrentActive()) ) {
    		log.warn("下注 失败 局号不符, {}");
			return "下注 失败 局号不符";
		}
		
		Date now = new Date();
		
		if ( gameInfo.getStatus()!= GameStatusEnum.Betting || (gameInfo.getEndTime()!=null && now.after(gameInfo.getEndTime())) ) {
    		log.warn("下注 失败 非下注状态, {}", gameInfo.getStatus().toString());
            return "下注 失败 非下注状态";
		}
    	
    	// 检核限红
    	GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(tgChatId);
    	/// 20220713 调整为玩法限红
//    	if ( groupInfo.checkTotalBetAmount(betVO.getTotalAmount(), betVO.getMaxShoeAmount().longValue())==false ) {
//            return ResponseUtil.custom(String.format("下注失败 达到当局最大投注 %s", betVO.getMaxShoeAmount()));
//    	}
    	
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.warn("[下注] user查无资料, id: {}", userIdOrName);
            return "玩家资料错误";
    	}
    	
    	if (Objects.equals(UserVO.Status.DISABLE.getCode(), userVO.getStatus())) {
    		log.warn("[下注] user状态停用");
            return "玩家资料错误";
    	}

    	Long userId = userVO.getId();
    	String userName = userVO.getUserName();
    	String nickName = userVO.getNickName();
    	GameUserInfo userInfo = groupInfo.getUserInfo(userVO.getId());

    	Map<String, Object> params = new HashMap<>();
		
		String result = HttpClient4Util.doGet(
				gameServerDomain + RequestPathEnum.GAME_ODDS_LIST.getApiName() +"?gameCode=BACC"); 

		if (!ValidateUtil.checkHttpResponse(action, result)) {
            return String.format("查询限红 失败, %s", StringUtils.defaultString(result));
		}

		List<BsOddsBO> limitList = JSONObject.parseObject(JSONObject.parseObject(result).getString("data"), new TypeReference<List<BsOddsBO>>(){});
    	
    	for (String betOption: betOptionList) {
			if(BetOption.Z_X.contains(betOption) && !StringUtils.isEmpty(userInfo.getBetHistoryString())){
				if(!userInfo.getBetOptionByUser(betOption)){
					return "下注失败，不允许对冲下注";
				}
			}

    		// BsOdds里 幸运6有两笔配置，只比对ss2
    		Optional<BsOddsBO> oddsBoOpt = limitList.stream().filter( 
    				odds -> (StringUtils.equalsIgnoreCase(betOption, odds.getRuleCode()) 
    					|| ("SS".equalsIgnoreCase(betOption) && "SS2".equalsIgnoreCase(odds.getRuleCode())))
    		).findFirst();
    		
    		BsOddsBO oddsBO = null;
    		if (oddsBoOpt.isPresent()) {
    			oddsBO = oddsBoOpt.get();
    		} else {
    			return String.format("查无限红 下注选项:%s", betOption);
    		}
    		
    		if ( oddsBO.getMinAmount()==null || oddsBO.getMinAmount()==null ) {
    			return "下注失败 限红配置有误";
    		}
    		
        	if (!userInfo.checkUserBetAmount(betOption, amount
					, oddsBO.getMinAmount().longValue(), oddsBO.getMaxAmount().longValue())) {
                return String.format("下注失败 限红单注 %s %s-%s", betOption, oddsBO.getMinAmount(), oddsBO.getMaxAmount());
        	}
    	}
    	
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);

		// gs下注
    	params = new HashMap<>();
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());

		params.put("isTgRequest", isTgRequest);
		params.put("tgChatId", tgChatId);
		params.put("clientIP", ip);
		params.put("userId", userId);  
		params.put("userName", userName); 
		params.put("nickName", nickName); 
		params.put("noRun", noRun);
		params.put("noActive", gameInfo.getCurrentActive());
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
			log.warn("\r\n===== 下注 失败, 下注api报错, 玩家id:{}, 局号:{}, 错误原因:{}", userId, gameInfo.getCurrentActive(), result);
            return String.format("下注 失败, %s", StringUtils.defaultString(result));
		}

		JSONObject betJson = JSONObject.parseObject(result);
		Long betId = betJson.getLong("data");

    	//  呼叫
    	//	会员管理 - 下分api
    	String withdrawResult = assetsBusiness.withdraw(userId, totalAmount, tableId, betId);
    	if ( StringUtils.isNotBlank(withdrawResult) ) {
    		log.warn("[下注] 下分失败");
    		params = new HashMap<>();
    		params.put("betId", betId);
    		
    		result = HttpClient4Util.doPost(
    				gameServerDomain + RequestPathEnum.ORDER_DELETE.getApiName(),
    				params);

    		if (!ValidateUtil.checkHttpResponse(action, result)) {
    			log.warn("\r\n===== 下注 失败, 下分api报错, betId:{}, 玩家id:{}, 局号:{}, 错误原因:{}", betId.toString(), userId, gameInfo.getCurrentActive(), result);
                return String.format("下注 失败, 必须人工删除bet, id:%s, %s", betId.toString(), StringUtils.defaultString(result));
    		}
            return withdrawResult;
    	}
    	
		gameInfo = gameInfoBusiness.calculateBetAmount(deskCode, tgChatId, userId, nickName, betOptionList, amount);
		
		UserBetVO userBetVO = UserBetVO.builder().amount(BigDecimal.valueOf(totalAmount))
				.userId(userId).betTime(DateUtil.dateToPatten(now))
				.tgUserId(userName).build();
		rabbitTemplate.convertAndSend(MqConstants.USER_BET_STATISTICS, JSONObject.toJSONString(userBetVO));
		
		log.info("下注 成功");
		return null;
	}
	
	public List<BetResponseVO> currentOrderList (Long tgChatId, Integer queryAmount) {
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("近期订单 失败 查不到玩家资料");
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
		return JSONObject.parseObject(json.getString("data"), new TypeReference<List<BetResponseVO>>(){});
	}
	
	public BigDecimal todayTotalWater (Long tgChatId) {
		String action = "当日流水";
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("{} 失败 查不到玩家资料", action);
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
		String action = "当日盈亏";
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("{} 失败 查不到玩家资料", action);
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
		String action = "玩家返水";
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("{} 失败 查不到玩家资料", action);
            return null;
    	}
    	
    	// 查询未返水注单
    	String result = HttpClient4Util.doGet(gameServerDomain + RequestPathEnum.ORDER_QUERY_BET_IS_NOT_RETURNED.getApiName() +"?userId=" +userVO.getId() +"&tgChatId=" +tgChatId);
        if (!ValidateUtil.checkHttpResponse(action, result)) {
        	return null;
        }

        JSONObject json = JSONObject.parseObject(result);
		List<BetResponseVO> responseList = JSONObject.parseObject(json.getString("data"), new TypeReference<List<BetResponseVO>>(){});
        BigDecimal totalReturnAmount = BigDecimal.ZERO;
        
        for ( BetResponseVO vo: responseList ) {
        	
        	// TODO 返水逻辑
        	
        	// 注单返水
            Map<String, Object> params = new HashMap<>();
            params.put("betId", vo.getId());
            params.put("userId", vo.getUserId());
            params.put("tgChatId", vo.getTgChatId());
            params.put("winAmount", vo.getWinAmount());
            result = HttpClient4Util.doPost(gameServerDomain + RequestPathEnum.ORDER_RETURN_AMOUNT.getApiName(), params);
            if (!ValidateUtil.checkHttpResponse(action, result)) {
                log.info("\r\n==== 返水失败, 注单id: {}, 返水api报错: {}", vo.getId(), result);
            	return null;
            }
            
            json = JSONObject.parseObject(result);
            BigDecimal returnAmount = json.getBigDecimal("data");
            totalReturnAmount = totalReturnAmount.add(returnAmount);
            
            // 下分(返水)
            String raResult = assetsBusiness.returnAmount(vo.getUserId(), returnAmount, vo.getId());
            if ( StringUtils.isNotBlank(raResult) ) {
                log.info("\r\n==== 返水失败, 注单id: {}, 下分api报错: {}", vo.getId(), raResult);
            	return null;
            }
            log.info("返水成功, 注单id: {}", vo.getId());
        }
        
        return totalReturnAmount;
	}

	public List<BsOddsVO> redLimit() {
		String result = HttpClient4Util.doGet(gameServerDomain + RequestPathEnum.GAME_ODDS_LIST);
		JSONObject json = JSONObject.parseObject(result);
		return JSONObject.parseObject(json.getString("data"), new TypeReference<List<BsOddsVO>>(){});
	}
}
