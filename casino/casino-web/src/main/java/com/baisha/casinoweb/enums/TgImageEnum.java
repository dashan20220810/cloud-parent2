package com.baisha.casinoweb.enums;

public enum TgImageEnum {

	/** 開新局 */
	OpenNewGame("startBetPicUrl"),
	;
	
	TgImageEnum( String key ) {
		this.key = key;
	}
	
	private String key;
	
	public String getKey() {
		return key;
	}
}
