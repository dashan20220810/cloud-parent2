package com.baisha.backendserver.model.bo.desk;

import com.baisha.backendserver.model.bo.BaseBO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GameOddsListBO extends BaseBO {

    // "游戏编码"
    private String gameCode;

    //"玩法编码"
    private String ruleCode;

    // "玩法名称"
    private String ruleName;

    // "赔率"
    private BigDecimal odds;
}
