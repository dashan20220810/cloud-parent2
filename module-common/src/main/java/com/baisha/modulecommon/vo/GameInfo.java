package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baisha.modulecommon.enums.GameStatusEnum;

import lombok.Data;

@Data
public class GameInfo implements Serializable {

	private static final long serialVersionUID = -1722371455986219844L;

	/** 当前局号 */
	private String currentActive;
	
	/** 前局游戏资讯 */ // 设置前请先清空当前局的前一局
	private GameInfo lastGameInfo;
	
	/** 游戏状态 */
	private GameStatusEnum status;
	
	private Date beginTime;
	
	private Map<Long, GameTgGroupInfo> tgGroupMap = new HashMap<>();
	
	public GameTgGroupInfo getTgGroupInfo ( Long tgGroupId ) {
		if ( tgGroupMap==null ) {
			tgGroupMap = new HashMap<>();
		}
		
		GameTgGroupInfo result = tgGroupMap.get(tgGroupId);
		if ( result==null ) {
			result = new GameTgGroupInfo(tgGroupId);
			tgGroupMap.put(tgGroupId, result);
		}
		return result;
	}
}
