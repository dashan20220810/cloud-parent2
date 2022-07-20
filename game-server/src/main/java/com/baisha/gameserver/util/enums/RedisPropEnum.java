package com.baisha.gameserver.util.enums;

import lombok.Getter;

@Getter
public enum RedisPropEnum {

	ReturnAmountMultiplier("gameRedisProp::ReturnAmountMultiplier"),
	;
	
	RedisPropEnum(String key) {
		this.key = key;
	}
	
	private String key;
}
