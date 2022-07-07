package com.baisha.userserver.model;

import com.baisha.userserver.util.constants.UserServerConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "用户中心-会员余额变动记录")
public class BalanceChange extends BaseEntity {

    @ApiModelProperty(value = "会员ID")
    @Column(columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @ApiModelProperty(value = "关联ID")
    @Column(columnDefinition = "bigint(20) comment '关联ID'")
    private Long relateId;

    @ApiModelProperty(value = "变动类型  1充值 2下注 3 派奖")
    @Column(columnDefinition = "tinyint(2) comment '类型  1充值 2下注 3派奖 4提现(下分) '")
    private Integer changeType;

    @ApiModelProperty(value = "收支类型 1收入 2支出")
    @Column(columnDefinition = "tinyint(2) comment '收支类型 1收入 2支出'")
    private Integer balanceType;

    @ApiModelProperty(value = "交易前金额")
    @Column(columnDefinition = "decimal(16,2) comment '交易前金额'")
    private BigDecimal beforeAmount;

    @ApiModelProperty(value = "变化金额")
    @Column(columnDefinition = "decimal(16,2) comment '金额 变化金额'")
    private BigDecimal amount;

    @ApiModelProperty(value = "交易后金额")
    @Column(columnDefinition = "decimal(16,2) comment '交易后金额'")
    private BigDecimal afterAmount;

    @ApiModelProperty(value = "备注信息")
    @Column(columnDefinition = "varchar(100) comment '备注信息'")
    private String remark;


    public static boolean checkBalanceType(Integer balanceType) {
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
