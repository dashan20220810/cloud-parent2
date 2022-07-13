package com.baisha.userserver.model;

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
@Table(name = "SsOrder", indexes = {@Index(columnList = "userId"), @Index(columnList = "orderNum")})
@org.hibernate.annotations.Table(appliesTo = "ss_order", comment = "订单")
@ApiModel(value = "用户-订单对象")
public class SsOrder extends BaseEntity {

    @ApiModelProperty(value = "订单编号")
    @Column(columnDefinition = "varchar(32) comment '订单编号'")
    private String orderNum;

    @ApiModelProperty(value = "会员ID")
    @Column(columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @ApiModelProperty(value = "TG用户ID")
    @Column(columnDefinition = "varchar(64) comment 'TG用户ID'")
    private String tgUserId;

    @ApiModelProperty(value = "金额")
    @Column(columnDefinition = "decimal(16,2) comment '金额'")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单类型 1充值 2提现")
    @Column(columnDefinition = "tinyint(2) comment '订单类型 1充值 2提现'")
    private Integer orderType;

    @ApiModelProperty(value = "订单状态")
    @Column(columnDefinition = "tinyint(2) comment '订单状态 （ 1等待 2成功 3失败 4取消）'")
    private Integer orderStatus;

    @ApiModelProperty(value = "调整类型")
    @Column(columnDefinition = "tinyint(2) comment '调整类型'")
    private Integer adjustmentType;

    @ApiModelProperty(value = "流水倍数 >=0 整数(1-3位)")
    @Column(columnDefinition = "tinyint(4) comment '流水倍数'")
    private Integer flowMultiple;

    @ApiModelProperty(value = "附件key")
    @Column(columnDefinition = "varchar(100) comment '附件key'")
    private String fileKey;

    @ApiModelProperty(value = "备注信息")
    @Column(columnDefinition = "varchar(100) comment '备注信息'")
    private String remark;


}
