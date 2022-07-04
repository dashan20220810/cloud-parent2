package com.baisha.modulecommon.enums;

import java.util.Arrays;
import java.util.List;

public enum BetStatusEnum {

    BET(1, "下注"),
    SETTLEMENT(2, "结算");

    private Integer code;
    private String name;

    BetStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static BetStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        BetStatusEnum[] types = BetStatusEnum.values();
        for (BetStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<BetStatusEnum> getList() {
        return Arrays.asList(values());
    }

}
