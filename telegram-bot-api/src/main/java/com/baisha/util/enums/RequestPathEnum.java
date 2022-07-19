package com.baisha.util.enums;

/**
 * 接口路径 枚举类
 */
public enum RequestPathEnum {
    TELEGRAM_REGISTER_USER("/user/registerTG"),
    TELEGRAM_LEFT_USER("/user/leftTG"),
    TELEGRAM_GET_BET_BOTS_BY_CHAT_ID("/user/botListByGroupId"),
    TELEGRAM_PROP_MAP("/prop/systemTg"),
    TELEGRAM_ORDER_BET("/order/bet"),
    TELEGRAM_ORDER_DAY_FLOW("/order/todayTotalWater"),
    TELEGRAM_ORDER_DAY_PROFIT("/order/todayTotalProfit"),
    TELEGRAM_ORDER_RETURN_AMOUNT("/order/returnAmount"),
    TELEGRAM_ORDER_RECENT_BET("/order/currentList"),
    TELEGRAM_ORDER_RED_LIMIT("/order/redLimit"),
    TELEGRAM_USER_BALANCE("/user/balance"),
    ;

    /**
     * 构造器
     */
    RequestPathEnum(String apiName) {
        this.apiName = apiName;
    }

    /**
     * apiName
     */
    String apiName;

    public String getApiName() {
        return apiName;
    }
}
