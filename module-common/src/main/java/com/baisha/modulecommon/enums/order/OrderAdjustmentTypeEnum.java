package com.baisha.modulecommon.enums.order;

import java.util.Arrays;
import java.util.List;


/**
 * @author yihui
 * 充值订单调整类型
 */

public enum OrderAdjustmentTypeEnum {

    ADMIN_CHARGE(1, "会员存款(后台)"),

    ;

    private Integer code;
    private String name;

    OrderAdjustmentTypeEnum(Integer code, String name) {
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

    public static OrderAdjustmentTypeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        OrderAdjustmentTypeEnum[] types = OrderAdjustmentTypeEnum.values();
        for (OrderAdjustmentTypeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<OrderAdjustmentTypeEnum> getList() {
        return Arrays.asList(values());
    }
}
