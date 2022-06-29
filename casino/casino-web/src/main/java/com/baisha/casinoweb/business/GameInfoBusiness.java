package com.baisha.casinoweb.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.GameTgGroupInfo;
import com.baisha.modulespringcacheredis.util.RedisUtil;

@Component
public class GameInfoBusiness {

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
    public synchronized GameInfo calculateBetAmount ( String deskCode, Long tgGroupId, Long userId, Long amount ) {
		GameInfo gameInfo = getGameInfo(deskCode);
		GameTgGroupInfo groupInfo = gameInfo.getTgGroupInfo(tgGroupId);
		groupInfo.addTotalBetAmount(amount);
		groupInfo.addUserBetAmount(userId, amount);
		setGameInfo(deskCode, gameInfo);
    	return gameInfo;
    }
    
}
