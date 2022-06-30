package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class GameTgGroupInfo implements Serializable {

	private static final long serialVersionUID = -4877268597655678066L;

	private Long tgGroupId;
	
	/** 当局下注累计 */
	private Long totalBetAmount;
	
	private Map<Long, GameUserInfo> userMap;

	public GameTgGroupInfo(Long tgGroupId) {
		super();
		this.tgGroupId = tgGroupId;
	}
	
	public GameUserInfo getUserInfo ( Long userId ) {
		if ( userMap==null ) {
			userMap = new HashMap<>();
		}
		
		GameUserInfo result = userMap.get(userId);
		if ( result==null ) {
			result = new GameUserInfo(userId);
			userMap.put(userId, result);
		}
		return result;
	}
	
	/**
	 * 计算当局下注
	 * @param betAmount
	 */
	public void addTotalBetAmount ( Long betAmount ) {
		if ( totalBetAmount==null ) {
			totalBetAmount = 0L;
		}
		
		totalBetAmount += betAmount;
	}
	
	/**
	 * 检核当局最大投注
	 * @param plusAmount
	 * @param limitStakeTotal
	 * @return
	 */
	public boolean checkTotalBetAmount ( Long plusAmount, Long limitStakeTotal ) {
		if ( totalBetAmount==null ) {
			totalBetAmount = 0L;
		}
		
		return limitStakeTotal >= (totalBetAmount + plusAmount);
	}
	
	public List<Map<String, Object>> getTop20BetUserData () {
		if ( userMap==null || userMap.size()==0 ) {
			return new ArrayList<>();
		}
		
		Collection<GameUserInfo> userInfoList = userMap.values();
		
		return userInfoList.stream().sorted(Comparator.comparingLong(GameUserInfo::getTotalBetAmount)).limit(20)
			.map( userInfo -> {
				Map<String, Object> map = new HashMap<>();
				map.put("username", userInfo.getNickName());
				map.put("betCommand", userInfo.getBetHistoryString());
				return map;
			})
			.collect(Collectors.toList());
	}
}
