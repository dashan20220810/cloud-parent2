package com.baisha.casinoweb.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulespringcacheredis.util.RedisUtil;

@Component
public class GameInfoBusiness {

    @Autowired
    private RedisUtil redisUtil;

    public synchronized GameInfo getGameInfo( Long tgChatId ) {
    	GameInfo result = (GameInfo) redisUtil.hget(RedisKeyConstants.SYS_GAME_INFO, String.valueOf(tgChatId));
		if ( result==null ) {
			result = new GameInfo();
			redisUtil.hset(RedisKeyConstants.SYS_GAME_INFO, String.valueOf(tgChatId), result);
		}
		
		return result;
    }

    public synchronized void setGameInfo( Long tgChatId, GameInfo gameInfo ) {
		redisUtil.hset(RedisKeyConstants.SYS_GAME_INFO, String.valueOf(tgChatId), gameInfo);
    }
    
    
}
