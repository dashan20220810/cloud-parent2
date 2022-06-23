package com.baisha.util.enums;

/**
 * 接口路径 枚举类
 */
public enum RequestPathEnum {
    TELEGRAM_REGISTER_USER("/user/registerTG"),
    TELEGRAM_ORDER_BET("/order/bet"),
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
