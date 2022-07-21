package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baisha.modulecommon.enums.GameStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  TgGameInfo implements Serializable {

	private static final long serialVersionUID = -1722371455986219844L;

	/** 当前局号 */
	private String currentActive;
	
	/** 前局游戏资讯 */ // 设置前请先清空当前局的前一局
//	private GameInfo lastGameInfo;
	
	/** 游戏状态 */
	private GameStatusEnum status;
	
	private Date beginTime;
	
	/** 封盘时间 */
	private Date endTime;
	
	private Map<Long, GameTgGroupInfo> tgGroupMap;
	
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
	
	public void initTgGRoupMap ( List<Long> tgGroupIdList ) {
		if ( tgGroupMap==null ) {
			tgGroupMap = new HashMap<>();
		}
		
		for ( Long tgGroupId: tgGroupIdList ) {
			GameTgGroupInfo groupInfo = new GameTgGroupInfo(tgGroupId);
			groupInfo.setTotalBetAmount(0L);
			tgGroupMap.put(tgGroupId, groupInfo);
		}
	}
}
