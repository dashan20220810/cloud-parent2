package com.baisha.modulecommon.enums;

import com.baisha.modulecommon.Constants;
import com.beust.jcommander.internal.Lists;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @Author 小智
 * @Date 13/7/22 4:42 PM
 * @Version 1.0
 */
@Getter
public enum OpenCardConvertSettleEnum {

    ZERO("0", TgBaccRuleEnum.SS2),

    ONE("1", TgBaccRuleEnum.Z),

    TWO("2", TgBaccRuleEnum.H),

    THREE("3", TgBaccRuleEnum.X),

    FOUR("4", TgBaccRuleEnum.ZD),

    FIVE("6", TgBaccRuleEnum.XD),

    SIX("7", TgBaccRuleEnum.SS3);


    private String code;

    private TgBaccRuleEnum openCard;

    OpenCardConvertSettleEnum(String code, TgBaccRuleEnum openCard) {
        this.code = code;
        this.openCard = openCard;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TgBaccRuleEnum getOpenCard() {
        return openCard;
    }

    public void setOpenCard(TgBaccRuleEnum openCard) {
        this.openCard = openCard;
    }

    public static String getAllOpenCardResult (final String openCard){
        List<String> openCardList = Lists.newArrayList();
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < openCard.length(); i++){
            openCardList.add(String.valueOf(openCard.charAt(i)));
        }
        for(String str : openCardList){
            TgBaccRuleEnum openResult = getCode(str);
            if(null == openResult){
                return null;
            }
            result.append(Objects.requireNonNull(getCode(str)).getCode()).append(Constants.COMMA_SEPARATED);
        }
        return result.substring(BigDecimal.ZERO.intValue(), result.length() - 1);
    }

    public static TgBaccRuleEnum getCode(final String openCard){
        for(OpenCardConvertSettleEnum openCardConvertEnum : OpenCardConvertSettleEnum.values()){
            if(openCard.contains(openCardConvertEnum.getCode())){
                return openCardConvertEnum.getOpenCard();
            }
        }
        return null;
    }
}
