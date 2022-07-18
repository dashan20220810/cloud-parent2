package com.baisha.modulecommon.enums;

import cn.hutool.core.util.ObjectUtil;
import com.baisha.modulecommon.Constants;
import com.beust.jcommander.internal.Lists;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author 小智
 * @Date 13/7/22 8:35 PM
 * @Version 1.0
 */
@Getter
public enum OpenCardConvertTgEnum {

    ZERO("0", TgBaccRuleEnum.SS2),

    ONE("1", TgBaccRuleEnum.Z),

    TWO("2", TgBaccRuleEnum.H),

    THREE("3", TgBaccRuleEnum.X),

    FOUR("4", TgBaccRuleEnum.ZD),

    FIVE("6", TgBaccRuleEnum.XD),

    SIX("7", TgBaccRuleEnum.SS3);


    private String code;

    private TgBaccRuleEnum openCardTg;

    OpenCardConvertTgEnum(String code, TgBaccRuleEnum openCardTg) {
        this.code = code;
        this.openCardTg = openCardTg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TgBaccRuleEnum getOpenCardTg() {
        return openCardTg;
    }

    public void setOpenCardTg(TgBaccRuleEnum openCardTg) {
        this.openCardTg = openCardTg;
    }

    public static String getAllOpenCardTgResult (final String openCardTg){
        List<String> openCardList = Lists.newArrayList();
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < openCardTg.length(); i++){
            openCardList.add(String.valueOf(openCardTg.charAt(i)));
        }
        for(String str : openCardList){
            TgBaccRuleEnum openResult = getName(str);
            if(null == openResult){
                return null;
            }
            result.append(getName(str)).append(Constants.COMMA_SEPARATED);
        }
        return result.substring(BigDecimal.ZERO.intValue(), result.length() - 1);
    }

    public static TgBaccRuleEnum getName(final String openCardTg){
        for(OpenCardConvertTgEnum openCardConvertTgEnum : OpenCardConvertTgEnum.values()){
            if(openCardTg.contains(openCardConvertTgEnum.getCode())){
                return openCardConvertTgEnum.getOpenCardTg();
            }
        }
        return null;
    }
}
