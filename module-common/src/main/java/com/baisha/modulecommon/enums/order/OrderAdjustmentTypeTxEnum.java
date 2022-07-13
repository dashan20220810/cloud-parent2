package com.baisha.modulecommon.enums.order;

import java.util.Arrays;
import java.util.List;


/**
 * @author yihui
 * 提现订单调整类型
 */

public enum OrderAdjustmentTypeTxEnum {

    ADMIN_CHARGE(1, "会员提款(后台)"),

    ;

    private Integer code;
    private String name;

    OrderAdjustmentTypeTxEnum(Integer code, String name) {
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

    public static OrderAdjustmentTypeTxEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        OrderAdjustmentTypeTxEnum[] types = OrderAdjustmentTypeTxEnum.values();
        for (OrderAdjustmentTypeTxEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<OrderAdjustmentTypeTxEnum> getList() {
        return Arrays.asList(values());
    }
}
