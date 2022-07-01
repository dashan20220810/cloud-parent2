package com.baisha.backendserver.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "sys_telegram_parameter", comment = "电报系统参数配置")
public class SysTelegramParameter extends BaseEntity {

    @Column(columnDefinition = "varchar(30) comment '唯一财务'")
    private String onlyFinance;

    @Column(columnDefinition = "varchar(30) comment '唯一客服'")
    private String onlyCustomerService;

    @Column(columnDefinition = "varchar(150) comment '开始下注图片路径' ")
    private String startBetPicUrl;

    //@Column(columnDefinition = "tinyint(4) comment '开始下注倒计时(秒)' ")
    //private Integer startBetSeventySeconds = 70;

    @Column(columnDefinition = "varchar(150) comment '开始下注倒计时路径' ")
    private String seventySecondsUrl;

    @Column(columnDefinition = "varchar(150) comment '博彩官方频道' ")
    private String officialGamingChannel;

    @Column(columnDefinition = "varchar(150) comment '开牌图片路径' ")
    private String openCardUrl;


}
