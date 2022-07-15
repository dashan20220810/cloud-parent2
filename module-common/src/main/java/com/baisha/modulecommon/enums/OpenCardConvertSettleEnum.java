package com.baisha.modulecommon.enums;

import com.baisha.modulecommon.Constants;
import com.beust.jcommander.internal.Lists;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 小智
 * @Date 13/7/22 4:42 PM
 * @Version 1.0
 */
@Getter
public enum OpenCardConvertSettleEnum {

    ZERO("0", TgBaccRuleEnum.SS2.getCode()),

    ONE("1", TgBaccRuleEnum.Z.getCode()),

    TWO("2", TgBaccRuleEnum.H.getCode()),

    THREE("3", TgBaccRuleEnum.X.getCode()),

    FOUR("4", TgBaccRuleEnum.ZD.getCode()),

    FIVE("6", TgBaccRuleEnum.XD.getCode()),

    SIX("7", TgBaccRuleEnum.SS3.getCode());


    private String code;

    private String openCard;

    OpenCardConvertSettleEnum(String code, String openCard) {
        this.code = code;
        this.openCard = openCard;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOpenCard() {
        return openCard;
    }

    public void setOpenCard(String openCard) {
        this.openCard = openCard;
    }

    public static String getAllOpenCardResult (final String openCard){
        List<String> openCardList = Lists.newArrayList();
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < openCard.length(); i++){
            openCardList.add(String.valueOf(openCard.charAt(i)));
        }
        for(String str : openCardList){
            result.append(getCode(str));
        }
        return result.substring(BigDecimal.ZERO.intValue(), result.length() - 1);
    }

    public static String getCode(final String openCard){
        for(OpenCardConvertSettleEnum openCardConvertEnum : OpenCardConvertSettleEnum.values()){
            if(openCard.contains(openCardConvertEnum.getCode())){
                return openCardConvertEnum.getOpenCard() + Constants.COMMA_SEPARATED;
            }
        }
        return null;
    }
}
