package com.baisha.modulecommon.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 余额改变类型
 */
public enum BalanceChangeEnum {

    RECHARGE(1, "充值"),
    BET(2, "下注"),
    WIN(3, "派彩"),
    WITHDRAW(4, "提现(下分)"),
    RETURN_AMOUNT(5, "返水"),

    //重新开奖
    BET_REOPEN(6, "扣除派彩(重新开牌)"), // 减余额
    BET_REWIN(7, "重新派彩"), // 重新派奖 加余额
    RETURN_AMOUNT_REOPEN(8, "扣除返水(重新开牌)"),// 减余额
    RETURN_REAMOUNT(9, "重新返水"),// 重新返水 加余额

    ;

    private Integer code;
    private String name;

    BalanceChangeEnum(Integer code, String name) {
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

    public static BalanceChangeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        BalanceChangeEnum[] types = BalanceChangeEnum.values();
        for (BalanceChangeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<BalanceChangeEnum> getList() {
        return Arrays.asList(values());
    }


}
