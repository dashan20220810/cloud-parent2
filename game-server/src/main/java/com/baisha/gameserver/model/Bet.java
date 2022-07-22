package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: alvin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "Bet", indexes = {@Index(columnList = "no_active"), @Index(columnList = "status"), @Index(columnList = "user_id")})
@ApiModel(value = "GS-Bet对象", description = "注单")
@Slf4j
public class Bet extends BaseEntity {

    private static final long serialVersionUID = -843697330512478843L;

    @ApiModelProperty("订单编号")
    @Column(name = "order_no", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '订单编号'")
    private String orderNo;

    @ApiModelProperty("user_id")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ApiModelProperty("user_name")
    @Column(name = "user_name", nullable = false)
    private String userName;

    @ApiModelProperty("nick_name")
    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @ApiModelProperty("tg_chat_id")
    @Column(name = "tg_chat_id", columnDefinition = "BIGINT COMMENT 'telegram群id'")
    private Long tgChatId;

//    @ApiModelProperty("下注类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,D对,SS幸运六,SB三宝")
//    @Column(name = "bet_option", nullable = false
//            , columnDefinition = "VARCHAR(10) COMMENT '下注类型: ZD庄对,XD闲对,Z庄,X闲,H和,H和,D对,SS幸运六,SB三宝'")
//    private String betOption;

    @ApiModelProperty("下注金额庄")
    @Column(name = "amount_z", columnDefinition = "BIGINT COMMENT '下注金额庄'", nullable = false)
    private Long amountZ = 0L;

    @ApiModelProperty("下注金额闲")
    @Column(name = "amount_x", columnDefinition = "BIGINT COMMENT '下注金额闲'", nullable = false)
    private Long amountX = 0L;

    @ApiModelProperty("下注金额和")
    @Column(name = "amount_h", columnDefinition = "BIGINT COMMENT '下注金额和'", nullable = false)
    private Long amountH = 0L;

    @ApiModelProperty("下注金额庄对")
    @Column(name = "amount_zd", columnDefinition = "BIGINT COMMENT '下注金额庄对'", nullable = false)
    private Long amountZd = 0L;

    @ApiModelProperty("下注金额闲对")
    @Column(name = "amount_xd", columnDefinition = "BIGINT COMMENT '下注金额闲对'", nullable = false)
    private Long amountXd = 0L;

    @ApiModelProperty("下注金额幸运六")
    @Column(name = "amount_ss", columnDefinition = "BIGINT COMMENT '下注金额幸运六'", nullable = false)
    private Long amountSs = 0L;

    @ApiModelProperty("客户端IP")
    @Column(name = "client_ip", columnDefinition = "VARCHAR(150) COMMENT '客户端IP'")
    private String clientIP;

    @ApiModelProperty("游戏轮号")
    @Column(name = "no_run", columnDefinition = "VARCHAR(50) COMMENT '游戏轮号'")
    private String noRun;

    @ApiModelProperty("游戏局号")
    @Column(name = "no_active", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功 2.结算完成)")
    @Column(name = "status", nullable = false, columnDefinition = "INT COMMENT '注單狀態(1.下注成功 2.结算完成)'")
    private Integer status;

    @ApiModelProperty(value = "输赢金额")
    @Column(columnDefinition = "decimal(16,2) comment '输赢金额'")
    private BigDecimal winAmount;

    @ApiModelProperty(value = "注单输赢结果(含派彩)")
    @Column(columnDefinition = "decimal(16,2) comment '注单输赢结果(含派彩)'")
    private BigDecimal finalAmount;

    @ApiModelProperty(value = "结算时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date settleTime;

    @ApiModelProperty(value = "已返水")
    @Column(columnDefinition = "VARCHAR(6) comment '已返水'")
    private Boolean isReturned = false;

    @ApiModelProperty("备注(结算)")
    @Column(name = "settle_remark", columnDefinition = "VARCHAR(100) COMMENT '备注(结算)'")
    private String settleRemark;

    @ApiModelProperty(value = "返水金额")
    @Column(columnDefinition = "decimal(16,2) comment '返水金额'")
    private BigDecimal returnAmount;


    /**
     * 检核下注请求
     *
     * @param bet
     * @return
     */
    public static boolean checkRequest(Bet bet) {

        if (bet.getUserId() == null) {
        	log.warn(" user id required ");
            return false;
        }

        if (StringUtils.isBlank(bet.getUserName())) {
        	log.warn(" user name required ");
            return false;
        }

//        if (StringUtils.isBlank(bet.getBetOption())) {
//            return false;
//        }

        if ((bet.getAmountH() + bet.getAmountSs() + bet.getAmountX() + bet.getAmountXd() + bet.getAmountZ() + bet.getAmountZd()) <= 0L) {
        	log.warn(" amount required ");
            return false;
        }

        if (StringUtils.isBlank(bet.getNoRun())) {
        	log.warn(" noRun required ");
            return false;
        }

        if (StringUtils.isBlank(bet.getNoActive())) {
        	log.warn(" noActive required ");
            return false;
        }

        if (StringUtils.isBlank(bet.getClientIP())) {
        	log.warn(" client ip required ");
            return false;
        }

        return true;
    }

    /**
     * 检核下注请求 game server
     *
     * @param bet
     * @return
     */
    public static boolean checkRequestForGs(Bet bet, boolean isTgRequest) {

        if (checkRequest(bet) == false) {
            return false;
        }

        if (StringUtils.isBlank(bet.getOrderNo())) {
        	log.warn(" order no required ");
            return false;
        }

        if (bet.getStatus() == null) {
        	log.warn(" status required ");
            return false;
        }

        if (isTgRequest && bet.getTgChatId() == null) {
        	log.warn(" tg chat id required ");
            return false;
        }

        return true;
    }

    /**
     * 返回流水 6个下注金额加总
     *
     * @return
     */
    public Long getFlowAmount() {
        return amountH + amountSs + amountX + amountXd + amountZ + amountZd;
    }

}
