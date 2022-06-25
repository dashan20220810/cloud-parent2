package com.baisha.userserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author yihui
 */
@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "user_telegram_relation", comment = "会员电报群关系")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"tgUserId", "tgGroupId"}))
@ApiModel(value = "会员中心-会员电报群关系")
public class UserTelegramRelation extends BaseEntity {

    @ApiModelProperty(value = "会员ID")
    @Column(columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @ApiModelProperty(value = "会员账号")
    @Column(columnDefinition = "varchar(30) comment '会员账号'")
    private String userName;

    @ApiModelProperty(value = "TG用户ID")
    @Column(columnDefinition = "varchar(64) comment 'TG用户ID'")
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID")
    @Column(columnDefinition = "varchar(64) comment 'TG群ID'")
    private String tgGroupId;

    @ApiModelProperty(value = "状态 1 正常 ，0离开")
    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0离开'")
    private Integer status = 1;

}
