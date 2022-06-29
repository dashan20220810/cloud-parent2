package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class GameTgGroupInfo implements Serializable {

	private static final long serialVersionUID = -4877268597655678066L;

	private Long tgGroupId;
	
	/** 当局下注累计 */
	private Long totalBetAmount;
	
	private Map<Long, Long> userBetAmountMap = new HashMap<>();

	public GameTgGroupInfo(Long tgGroupId) {
		super();
		this.tgGroupId = tgGroupId;
	}
	
	public Long getUserBetAmount ( Long userId ) {
		Long userBetAmount = userBetAmountMap.get(userId);
		return userBetAmount==null ? 0L : userBetAmount;
	}
	
	/**
	 * 计算玩家下注
	 * @param betAmount
	 */
	public void addTotalBetAmount ( Long betAmount ) {
		if ( totalBetAmount==null ) {
			totalBetAmount = 0L;
		}
		
		totalBetAmount += betAmount;
	}
	
	/**
	 * 计算当局下注
	 * @param userId
	 * @param betAmount
	 */
	public void addUserBetAmount ( Long userId, Long betAmount ) {
		Long userBetAmount = userBetAmountMap.get(userId);
		if ( userBetAmount==null ) {
			userBetAmount = 0L;
		}
		
		userBetAmount += betAmount;
		userBetAmountMap.put(userId, userBetAmount);
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
	
	/**
	 * 检核玩家限红
	 * @param userId
	 * @param plusAmount
	 * @param limitStakeMinAmount
	 * @param limitStakeMaxAmount
	 * @return
	 */
	public boolean checkUserBetAmount ( Long userId, Long plusAmount, Long limitStakeMinAmount, Long limitStakeMaxAmount) {
		Long userTotalBetAmount = getUserBetAmount(userId);
		return (limitStakeMinAmount <= (userTotalBetAmount+plusAmount)) && (limitStakeMaxAmount >= (userTotalBetAmount+plusAmount));
	}
}
