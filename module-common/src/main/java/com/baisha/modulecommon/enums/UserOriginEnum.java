package com.baisha.modulecommon.enums;

/**
 * 用户来源 枚举类
 */
public enum UserOriginEnum {
    TG_ORIGIN("telegram", "用户来源于TG"),
    ;

    /**
     * 构造器
     */
    UserOriginEnum(String origin, String describe) {
        this.origin = origin;
        this.describe = describe;
    }

    /**
     * 用户来源
     */
    String origin;
    /**
     * 描述
     */
    String describe;

    public String getOrigin() {
        return origin;
    }
    public String getDescribe() {
        return describe;
    }
}
