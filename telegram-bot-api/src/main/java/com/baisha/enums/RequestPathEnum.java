package com.baisha.enums;

/**
 * 接口路径 枚举类
 */
public enum RequestPathEnum {
    TELEGRAM_REGISTER_USER("/user/registerA"),
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
