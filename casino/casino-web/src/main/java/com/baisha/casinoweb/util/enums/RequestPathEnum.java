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
    /** 近期注单 */
    ORDER_CURRENT_LIST("/order/currentList"),
    /** 查询桌台 by ip */
    DESK_QUERY_BY_LOCAL_IP("/desk/queryByLocalIp"),
    /** 查询桌台 by DeskCode */
    DESK_QUERY_BY_DESK_CODE("/desk/queryByDeskCode"),
    /** 查询桌台 by id */
    DESK_QUERY_BY_ID("/desk/queryById"),
    
    /*
     * 後台
     */
    
    /*
     * telegram
     */
    /** 开新局 */
    TG_OPEN_NEW_GAME("/command/startNewBureau"),
    /** 封盘中 */ 
    TG_CLOSE_GAME("/command/sealingLine"),
    /** 取群id list */
    TG_GET_GROUP_ID_LIST("/tgChat/findByTableId"),
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
