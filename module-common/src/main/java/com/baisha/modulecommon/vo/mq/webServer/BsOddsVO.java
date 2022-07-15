package com.baisha.modulecommon.vo.mq.webServer;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BsOddsVO implements Serializable {

    private static final long serialVersionUID = -1745750356311418270L;

    /** 游戏编码 */
    private String gameCode;

    /** 玩法编码 */
    private String ruleCode;

    /** 玩法名称 */
    private String ruleName;

    /** 赔率 */
    private BigDecimal odds;

    /** 最大限红 */
    private Integer maxAmount;

    /** 最小限红 */
    private Integer minAmount;


}
