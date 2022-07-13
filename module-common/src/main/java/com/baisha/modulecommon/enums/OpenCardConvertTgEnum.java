package com.baisha.modulecommon.enums;

import com.baisha.modulecommon.Constants;
import com.beust.jcommander.internal.Lists;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 小智
 * @Date 13/7/22 8:35 PM
 * @Version 1.0
 */
@Getter
public enum OpenCardConvertTgEnum {

    ZERO("0", TgBaccRuleEnum.SS2.getName()),

    ONE("1", TgBaccRuleEnum.Z.getName()),

    TWO("2", TgBaccRuleEnum.H.getName()),

    THREE("3", TgBaccRuleEnum.X.getName()),

    FOUR("4", TgBaccRuleEnum.ZD.getName()),

    FIVE("5", TgBaccRuleEnum.XD.getName()),

    SIX("6", TgBaccRuleEnum.SS3.getName());


    private String code;

    private String openCardTg;

    OpenCardConvertTgEnum(String code, String openCardTg) {
        this.code = code;
        this.openCardTg = openCardTg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOpenCardTg() {
        return openCardTg;
    }

    public void setOpenCardTg(String openCardTg) {
        this.openCardTg = openCardTg;
    }

    public static String getAllOpenCardTgResult (final String openCardTg){
        List<String> openCardList = Lists.newArrayList();
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < openCardTg.length(); i++){
            openCardList.add(String.valueOf(openCardTg.charAt(i)));
        }
        for(String str : openCardList){
            result.append(getName(str));
        }
        return result.substring(BigDecimal.ZERO.intValue(), result.length() - 1);
    }

    public static String getName(final String openCardTg){
        for(OpenCardConvertTgEnum openCardConvertTgEnum : OpenCardConvertTgEnum.values()){
            if(openCardTg.contains(openCardConvertTgEnum.getCode())){
                return openCardConvertTgEnum.getOpenCardTg() + Constants.COMMA_SEPARATED;
            }
        }
        return null;
    }
}
