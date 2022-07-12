package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameUserInfo implements Serializable {

	private static final long serialVersionUID = 6944592346115670292L;
	
	private Long userId;
	
	private String nickName;
	
	private List<Map<String, Object>> betHistory = new ArrayList<>();
	
	private Long totalBetAmount = 0L;

	public GameUserInfo(Long userId) {
		super();
		this.userId = userId;
	}
	
	public void addBetHistory ( String betOption, Long amount ) {
		Map<String, Object> record = new HashMap<>();
		record.put("betOption", betOption);
		record.put("amount", amount);
		betHistory.add(record);
	}
	
	public void addTotalBetAmount ( Long amount ) {
		totalBetAmount += amount;
	}
	
	/**
	 * 检核玩家限红
	 * @param plusAmount
	 * @param limitStakeMinAmount
	 * @param limitStakeMaxAmount
	 * @return
	 */
	public boolean checkUserBetAmount ( Long plusAmount, Long limitStakeMinAmount, Long limitStakeMaxAmount) {
		return (limitStakeMinAmount <= (totalBetAmount+plusAmount)) && (limitStakeMaxAmount >= (totalBetAmount+plusAmount));
	}
	
	public String getBetHistoryString() { 
		StringBuilder result = new StringBuilder();
		
		if ( CollectionUtils.isEmpty(betHistory)==false ) {
			for ( Map<String, Object> map: betHistory ) {
				result.append(map.get("betOption")).append(map.get("amount")).append(" ");
			}
		}
		
		return result.toString().trim();
	}
}
