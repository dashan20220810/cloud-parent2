package com.baisha.userserver.model;

import com.baisha.userserver.constants.UserServerConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@Table(name = "BalanceChange", indexes = {@Index(columnList = "userId")})
public class BalanceChange extends BaseEntity {

    /**
     * 用户id
     */
    @Column(precision = 20)
    private Long userId;

    /**
     * 收支类型 1收入 2支出
     */
    @Column(precision = 1)
    private Integer balanceType;

    /**
     * 交易前金额
     */
    @Column(precision = 16, scale = 2)
    private BigDecimal beforeAmount;

    /**
     * 金额 变化金额
     */
    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    /**
     * 交易后金额
     */
    @Column(precision = 16, scale = 2)
    private BigDecimal afterAmount;

    /**
     * 备注信息
     */
    @Column(length = 100)
    private String remark;


    public static boolean checkBalanceType(Integer balanceType) {
        if (null == balanceType) {
            return true;
        }
        if (balanceType != UserServerConstants.STATUS_NORMAL && balanceType != UserServerConstants.STATUS_DISABLED) {
            return true;
        }
        return false;
    }

    /**
     * 验证金额
     *
     * @param decimal
     * @return
     */
    public static boolean checkAmount(BigDecimal decimal) {
        if (null == decimal) {
            return true;
        }
        if (decimal.compareTo(BigDecimal.ZERO) <= 0) {
            return true;
        }
        return false;
    }


}
