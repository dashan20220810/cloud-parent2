package com.baisha.gameserver.vo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BetReturnAmountVO {

	private Long userId;
	
	private Long tgChatId;
	
	/** 返水总额 */
	private Long totalFlowAmount;
	
	private BigDecimal totalReturnAmount;

	public BetReturnAmountVO(Long userId, Long tgChatId, Long totalFlowAmount) {
		super();
		this.userId = userId;
		this.tgChatId = tgChatId;
		this.totalFlowAmount = totalFlowAmount;
	}
}
