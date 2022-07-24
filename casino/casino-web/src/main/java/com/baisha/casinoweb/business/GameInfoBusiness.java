package com.baisha.casinoweb.business;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.baisha.modulecommon.BigDecimalConstants;
import com.baisha.modulecommon.vo.*;
import org.redisson.api.RBucket;
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

    public synchronized TgGameInfo getTgGameInfo( final String gameKey) {
		Optional<RMapCache<String, TgGameInfo>> map = Optional.of(redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_INFO));
		return map.map(obj -> obj.get(gameKey)).orElse(new TgGameInfo());
    }

    public synchronized void setTgGameInfo( final String gameKey, final TgGameInfo gameInfo ) {
		RMapCache<String, TgGameInfo> map = redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_INFO);
		map.put(gameKey, gameInfo, BigDecimalConstants.ONE.longValue(), TimeUnit.DAYS);
    }
    
    /**
     * 计算game info中下注金额
     * @param noActive
     * @param userId
     * @param amount
     * @return
     */
    public synchronized TgGameInfo calculateBetAmount ( String noActive, Long tgGroupId, Long userId, String nickName, List<String> betOptionList
    		, Long amount ) {
		TgGameInfo gameInfo = getTgGameInfo(noActive);
		GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(tgGroupId);
		GameUserInfo userInfo = groupInfo.getUserInfo(userId);
		userInfo.setNickName(nickName);
		
		for ( String betOption: betOptionList ) {
			groupInfo.addTotalBetAmount(amount);
			userInfo.addTotalBetAmount(amount);
			userInfo.addBetHistory(betOption, amount);
    		userInfo.addOptionAmount(betOption, amount);
		}
		setTgGameInfo(noActive, gameInfo);
    	return gameInfo;
    }
    
    public void closeGame ( final String noActive ) {
    	Map<String, Object> result = new HashMap<>();
    	Map<Long, Map<String, Object>> groupTeamMap = new HashMap<>();
		TgGameInfo tgGameInfo = getTgGameInfo(noActive);
		tgGameInfo.setStatus(GameStatusEnum.StopBetting);
    	
		result.put("bureauNum", noActive);
		
		if ( tgGameInfo.getTgGroupMap()!=null ) {
			Set<Long> tgGroupIdSet = tgGameInfo.getTgGroupMap().keySet();
			for ( Long tgGroupId: tgGroupIdSet ) {
				Map<String, Object> groupMap = new HashMap<>();
				GameTgGroupInfo groupInfo = tgGameInfo.getTgGroupInfo(tgGroupId);
				
				groupMap.put("totalBetAmount", groupInfo.getTotalBetAmount());
				groupMap.put("top20Users", groupInfo.getTop20BetUserData());
				groupTeamMap.put(tgGroupId, groupMap);
			}
		}
		
		result.put("tgBetInfo", groupTeamMap);
		setTgGameInfo(noActive, tgGameInfo);
		
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

	public synchronized String getGameResult(final String currentActive) {
		Optional<RMapCache<String, String>> map = Optional.of(redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_RESULT));
		return map.map(obj -> obj.get(currentActive)).orElse(null);
	}

	public synchronized void setGameInfo(String gameTimeKey, GameInfo newGameInfo) {
		RMapCache<String, GameInfo> map = redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_TIME);
		map.fastPut(gameTimeKey, newGameInfo, BigDecimalConstants.ONE.longValue(), TimeUnit.DAYS);
	}

	public synchronized GameInfo getGameInfo(String gameTimeKey) {
		Optional<RMapCache<String, GameInfo>> map = Optional.of(redisUtil.getMapCache(RedisKeyConstants.SYS_GAME_TIME));
		return map.map(obj -> obj.get(gameTimeKey)).orElse(new GameInfo());
	}
}
