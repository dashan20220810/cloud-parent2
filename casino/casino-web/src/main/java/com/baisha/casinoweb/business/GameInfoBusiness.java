package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.GameTgGroupInfo;
import com.baisha.modulecommon.vo.GameUserInfo;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GameInfoBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;

    @Autowired
    private RedisUtil redisUtil;

    public synchronized GameInfo getGameInfo( String deskCode ) {
    	GameInfo result = (GameInfo) redisUtil.hget(RedisKeyConstants.SYS_GAME_INFO, deskCode);
		if ( result==null ) {
			result = new GameInfo();
			redisUtil.hset(RedisKeyConstants.SYS_GAME_INFO, deskCode, result);
		}
		
		return result;
    }

    public synchronized void setGameInfo( String deskCode, GameInfo gameInfo ) {
		redisUtil.hset(RedisKeyConstants.SYS_GAME_INFO, deskCode, gameInfo);
    }
    
    /**
     * 计算game info中下注金额
     * @param deskCode
     * @param userId
     * @param amount
     * @return
     */
    public synchronized GameInfo calculateBetAmount ( String deskCode, Long tgGroupId, Long userId, String nickName, String betOption
    		, Long amount ) {
		GameInfo gameInfo = getGameInfo(deskCode);
		GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(tgGroupId);
		groupInfo.addTotalBetAmount(amount);
		GameUserInfo userInfo = groupInfo.getUserInfo(userId);
		userInfo.setNickName(nickName);
		userInfo.addTotalBetAmount(amount);
		userInfo.addBetHistory(betOption, amount);
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
    		return;
		}
    }
    
}
