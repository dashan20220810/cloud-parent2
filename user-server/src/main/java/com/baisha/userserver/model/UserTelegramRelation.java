package com.baisha.userserver.model;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "tgGroupId"}))
public class UserTelegramRelation extends BaseEntity {

    @Column(columnDefinition = "bigint(20) comment '会员ID'")
    private Long userId;

    @Column(columnDefinition = "varchar(30) comment '会员账号'")
    private String userName;

    @Column(columnDefinition = "varchar(64) comment 'TG用户ID'")
    private String tgUserId;

    @Column(columnDefinition = "varchar(64) comment 'TG群ID'")
    private String tgGroupId;

    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0离开'")
    private Integer status = 1;

}
