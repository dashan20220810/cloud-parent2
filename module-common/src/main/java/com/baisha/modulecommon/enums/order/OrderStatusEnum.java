package com.baisha.modulecommon.enums.order;

import java.util.Arrays;
import java.util.List;

/**
 * 订单状态
 *
 * @author yihui
 */

public enum OrderStatusEnum {

    ORDER_WAIT(1, "等待确认"),
    ORDER_SUCCESS(2, "成功"),
    ORDER_FAIL(3, "失败"),
    ORDER_CANCLE(4, "取消"),

    ;

    private Integer code;
    private String name;

    OrderStatusEnum(Integer code, String name) {
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

    public static OrderStatusEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        OrderStatusEnum[] types = OrderStatusEnum.values();
        for (OrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<OrderStatusEnum> getList() {
        return Arrays.asList(values());
    }
}
