package com.baisha.modulecommon.enums;

import lombok.Getter;

/**
 * @author alvin
 *	bet.balanceType enum
 */
@Getter
public enum BalanceTypeEnum {

	/** 收入 */
	INCOME(1, "收入"),
	/** 支出 */
	EXPENSES(2, "支出"),
	
	;
	
	private Integer code;
	private String name;
	
	BalanceTypeEnum( Integer code, String name ) {
		this.code = code;
		this.name = name;
	}
	
}
