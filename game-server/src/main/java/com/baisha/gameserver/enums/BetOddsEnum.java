package com.baisha.gameserver.enums;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 玩法倍率
 *
 * @author yihui
 */
@Getter
public enum BetOddsEnum {
    //Z（庄）  X（闲）  H（和） ZD（庄对） XD（闲对） D（庄对 闲对）SB（庄对 闲对 和）SS（幸运6)
    //不免佣金
    Z("Z", new BigDecimal("0.95")),
    X("X", new BigDecimal("1.0")),
    H("H", new BigDecimal("8.0")),
    ZD("ZD", new BigDecimal("11.0")),
    XD("XD", new BigDecimal("11.0")),
    SS("SS", new BigDecimal("20.0")),

    //SS2("SS2", new BigDecimal("12.0")),  //幸运6 2张牌
    ;

    private String code;
    private BigDecimal odds;

    BetOddsEnum(String code, BigDecimal odds) {
        this.code = code;
        this.odds = odds;
    }

    public static BetOddsEnum getBetOddsByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        BetOddsEnum[] types = BetOddsEnum.values();
        for (BetOddsEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

    public static List<BetOddsEnum> getList() {
        return Arrays.asList(values());
    }
}
