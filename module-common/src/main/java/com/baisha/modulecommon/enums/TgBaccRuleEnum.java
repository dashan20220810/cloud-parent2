package com.baisha.modulecommon.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 电报百家乐玩法
 */
@Getter
public enum TgBaccRuleEnum {
    
    Z("Z", "庄"),
    X("X", "闲"),
    H("H", "和"),
    ZD("ZD", "庄对"),
    XD("XD", "闲对"),
    SS2("SS2", "幸运六(二张牌)"),
    SS3("SS3", "幸运六(三张牌)"),
    ;

    private String code;
    private String name;

    TgBaccRuleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TgBaccRuleEnum nameOfCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        TgBaccRuleEnum[] types = TgBaccRuleEnum.values();
        for (TgBaccRuleEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }


}
