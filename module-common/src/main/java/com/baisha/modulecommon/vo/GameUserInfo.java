package com.baisha.modulecommon.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baisha.modulecommon.enums.BetOption;
import org.springframework.util.CollectionUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameUserInfo implements Serializable {

	private static final long serialVersionUID = 6944592346115670292L;
	
	private Long userId;
	
	private String nickName;
	
	/**
	 * 玩家下注记录
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<Map<String, Object>> betHistory = new ArrayList<>();

	/**
	 * 各玩法下注金额累计
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Map<String, Long> optionAmountMap = new HashMap<>();
	
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
	
	public void addOptionAmount ( String option, Long plusAmount ) {
		Long optionAmount = optionAmountMap.get(option);
		if ( optionAmount==null ) {
			optionAmount = 0L;
		}
		
		optionAmountMap.put(option, optionAmount+plusAmount);
	}
	
	public Long getOptionAmount ( String option ) {
		Long optionAmount = optionAmountMap.get(option);
		if ( optionAmount==null ) {
			return 0L;
		}
		return optionAmount;
	}
	
	/**
	 * 检核玩家限红
	 * @param plusAmount
	 * @param limitStakeMinAmount
	 * @param limitStakeMaxAmount
	 * @return
	 */
	public boolean checkUserBetAmount ( String option, Long plusAmount, Long limitStakeMinAmount, Long limitStakeMaxAmount) {
		Long optionAmount = getOptionAmount(option);
//		return (limitStakeMinAmount <= (totalBetAmount+plusAmount)) && (limitStakeMaxAmount >= (totalBetAmount+plusAmount));
		return (limitStakeMinAmount <= (optionAmount+plusAmount)) && (limitStakeMaxAmount >= (optionAmount+plusAmount));
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

	public Boolean getBetOptionByUser(final String betOption){
		List<Map<String, Object>> userBetOptions = betHistory.stream()
				.filter(obj -> BetOption.Z_X.contains(obj.get("betOption") == null ? null
						: obj.get("betOption"))).toList();
		if(CollectionUtils.isEmpty(userBetOptions)){
			return true;
		}else{
			return userBetOptions.stream().
					anyMatch(obj -> obj.get("betOption").equals(betOption));
		}
	}
}
