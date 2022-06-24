package com.baisha.util.enums;

/**
 * 接口路径 枚举类
 */
public enum RequestPathEnum {
    TELEGRAM_REGISTER_USER("/user/registerTG"),
    TELEGRAM_PROP_CUSTOMER("/prop/customer"),
    TELEGRAM_PROP_FINANCE("/prop/finance"),
    TELEGRAM_TG_IMAGE("/tg/image"),
    TELEGRAM_TG_CURRENT_ACTIVE("/g/currentActive"),
    TELEGRAM_TG_LIMIT_STAKES("/g/limitStakes"),
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
