package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.baisha.modulecommon.BigDecimalConstants;
import com.baisha.modulecommon.vo.*;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GameInfoBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;

    @Value("${project.game.settle-buffer-time-seconds}")
    private Integer gameSettleBufferTimeSeconds;

    @Autowired
    private RedissonClient redisUtil;

    public synchronized GameInfo getGameInfo( final String gameKey) {
		RMap<String, GameInfo> map = redisUtil.getMap(RedisKeyConstants.SYS_GAME_INFO);
		return map.get(gameKey);
    }

    public synchronized void setGameInfo( final String gameKey, final GameInfo gameInfo ) {
		RMap<String, GameInfo> map = redisUtil.getMap(RedisKeyConstants.SYS_GAME_INFO);
		map.put(gameKey, gameInfo);
    }
    
    /**
     * 计算game info中下注金额
     * @param deskCode
     * @param userId
     * @param amount
     * @return
     */
    public synchronized GameInfo calculateBetAmount ( String deskCode, Long tgGroupId, Long userId, String nickName, List<String> betOptionList
    		, Long amount ) {
		GameInfo gameInfo = getGameInfo(deskCode);
		GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(tgGroupId);
		GameUserInfo userInfo = groupInfo.getUserInfo(userId);
		userInfo.setNickName(nickName);
		
		for ( String betOption: betOptionList ) {
			groupInfo.addTotalBetAmount(amount);
			userInfo.addTotalBetAmount(amount);
			userInfo.addBetHistory(betOption, amount);
    		userInfo.addOptionAmount(betOption, amount);
		}
		setGameInfo(deskCode, gameInfo);
    	return gameInfo;
    }
    
    public void closeGame ( String deskCode ) {
    	Map<String, Object> result = new HashMap<>();
    	Map<Long, Map<String, Object>> groupTeamMap = new HashMap<>();
		GameInfo gameInfo = getGameInfo(deskCode);
		gameInfo.setStatus(GameStatusEnum.StopBetting);
    	
		result.put("bureauNum", gameInfo.getCurrentActive());
		
		if ( gameInfo.getTgGroupMap()!=null ) {
			Set<Long> tgGroupIdSet = gameInfo.getTgGroupMap().keySet();
			for ( Long tgGroupId: tgGroupIdSet ) {
				Map<String, Object> groupMap = new HashMap<>();
				GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(tgGroupId);
				
				groupMap.put("totalBetAmount", groupInfo.getTotalBetAmount());
				groupMap.put("top20Users", groupInfo.getTop20BetUserData());
				groupTeamMap.put(tgGroupId, groupMap);
			}
		}
		
		result.put("tgBetInfo", groupTeamMap);
		setGameInfo(deskCode, gameInfo);
		
		try {
			Thread.sleep( gameSettleBufferTimeSeconds*1000 );
		} catch (InterruptedException e) {
			log.error("封盘 失败", e);
		}
		
    	log.info("\r\n================= 封盘");
		String response = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_CLOSE_GAME.getApiName(),
				JSONObject.toJSONString(result));

        if (CommonUtil.checkNull(response)) {
        	log.warn("封盘 失败");
    		return;
        }
        
		JSONObject json = JSONObject.parseObject(response);
		Integer code = json.getInteger("code");

		if ( code!=0 ) {
        	log.warn("封盘 失败, {}", response);
		}
    }

	public synchronized void setGameResult(final String currentActive, final String openCardResult) {
		RMapCache<String, String> map = redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_RESULT);
		map.put(currentActive, openCardResult, BigDecimalConstants.ONE.longValue(), TimeUnit.DAYS);
	}

	public synchronized void getGameResult(final String currentActive) {
		RMapCache<String, String> map = redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_RESULT);
		map.get(currentActive);
	}

	public synchronized void setGameTime(String gameTimeKey, NewGameInfo newGameInfo) {
		RMapCache<String, NewGameInfo> map = redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_TIME);
		map.fastPut(gameTimeKey, newGameInfo, BigDecimalConstants.ONE.longValue(), TimeUnit.DAYS);
	}

	public synchronized NewGameInfo getGameTime(String gameTimeKey) {
		RMapCache<String, NewGameInfo> map = redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_TIME);
		return map.get(gameTimeKey);
	}
}
