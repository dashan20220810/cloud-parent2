package com.baisha.modulecommon.enums;

import java.util.Arrays;
import java.util.List;

/**
 * 打码量改变类型
 */
public enum PlayMoneyChangeEnum {

    RECHARGE(1, "充值"),
    SETTLEMENT(2, "结算"),

    //重新开奖
    BET_REOPEN(3, "重新开牌"), //加打码量
    SETTLEMENT_REOPEN(4, "重新结算"), //减打码量


    ;
    private Integer code;
    private String name;

    PlayMoneyChangeEnum(Integer code, String name) {
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

    public static PlayMoneyChangeEnum nameOfCode(Integer code) {
        if (null == code) {
            return null;
        }
        PlayMoneyChangeEnum[] types = PlayMoneyChangeEnum.values();
        for (PlayMoneyChangeEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<PlayMoneyChangeEnum> getList() {
        return Arrays.asList(values());
    }


}
