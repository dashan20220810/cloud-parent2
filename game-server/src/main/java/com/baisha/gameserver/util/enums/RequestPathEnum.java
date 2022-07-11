package com.baisha.gameserver.util.enums;

public enum RequestPathEnum {

	/** 上下分 */
    ASSETS_BALANCE("/assets/balance"),
    ;
    
    /**
     * 构造器
     */
    RequestPathEnum(String apiName) {
        this.apiName = apiName;
    }

    /**
     * key
     */
    String apiName;

    public String getApiName() {
        return apiName;
    }
}
