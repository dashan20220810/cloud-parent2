package com.baisha.casinoweb.util.enums;

/**
 * 接口路径 枚举类
 */
public enum RequestPathEnum {
	
	/*
	 * 使用者中心  
	 */
	/** 使用者注册 */
    USER_REGISTER("/user/save"),
    /** id查找使用者 */
    USER_QUERY_BY_ID("/user/findById"),
    /** user_name查找使用者 */
    USER_QUERY_BY_USER_NAME("/user/query"),
	/** 使用者登入 */
    USER_LOGIN("/user/login"), // TODO apiName
	/** 上下分 */
    ASSETS_BALANCE("/assets/balance"),
    /** 余額 */
    ASSETS_QUERY("/assets/query"),

	/*
	 * 游戏中心  
	 */
    /** 下注 */
    ORDER_BET("/order/bet"),
    
    /*
     * 後台
     */
    /** 限紅 */
    LIMIT_STAKES(""),
    
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
