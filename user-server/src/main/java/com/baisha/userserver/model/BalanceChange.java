package com.baisha.userserver.model;

import com.baisha.userserver.util.constants.UserServerConstants;
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
@org.hibernate.annotations.Table(appliesTo = "balance_change", comment = "会员余额变动记录")
public class BalanceChange extends BaseEntity {

    @Column(columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @Column(columnDefinition = "tinyint(2) comment '收支类型 1收入 2支出'")
    private Integer balanceType;

    @Column(columnDefinition = "decimal(16,2) comment '交易前金额'")
    private BigDecimal beforeAmount;

    @Column(columnDefinition = "decimal(16,2) comment '金额 变化金额'")
    private BigDecimal amount;

    @Column(columnDefinition = "decimal(16,2) comment '交易后金额'")
    private BigDecimal afterAmount;

    @Column(columnDefinition = "varchar(100) comment '备注信息'")
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
