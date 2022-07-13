package com.baisha.modulecommon.enums.order;

import java.util.Arrays;
import java.util.List;

/**
 * 订单类型
 *
 * @author yihui
 */

public enum OrderTypeEnum {

    CHARGE_ORDER(1, "人工充值"),
    WITHDRAW_ORDER(2, "提现"),

    ;

    private Integer code;
    private String name;

    OrderTypeEnum(Integer code, String name) {
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

    public static OrderTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        OrderTypeEnum[] types = OrderTypeEnum.values();
        for (OrderTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<OrderTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
