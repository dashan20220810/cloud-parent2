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
@Table(name = "PlayMoneyChange", indexes = {@Index(columnList = "userId")})
@org.hibernate.annotations.Table(appliesTo = "play_money_change", comment = "会员打码量变动记录")
public class PlayMoneyChange extends BaseEntity {

    @Column(columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @Column(columnDefinition = "bigint(20) comment '关联ID'")
    private Long relateId;

    @Column(columnDefinition = "tinyint(2) comment '类型  1充值 2结算'")
    private Integer changeType;

    @Column(columnDefinition = "tinyint(2) comment '收支类型 1收入 2支出'")
    private Integer playMoneyType;

    @Column(columnDefinition = "decimal(16,2) comment '交易前金额'")
    private BigDecimal beforeAmount;

    @Column(columnDefinition = "decimal(16,2) comment '金额 变化金额'")
    private BigDecimal amount;

    @Column(columnDefinition = "decimal(16,2) comment '交易后金额'")
    private BigDecimal afterAmount;

    @Column(columnDefinition = "varchar(100) comment '备注信息'")
    private String remark;


    public static boolean checkPlayMoneyType(Integer balanceType) {
        if (null == balanceType) {
            return true;
        }
        if (balanceType != UserServerConstants.INCOME && balanceType != UserServerConstants.EXPENSES) {
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
