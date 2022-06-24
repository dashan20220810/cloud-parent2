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
@org.hibernate.annotations.Table(appliesTo = "login_log", comment = "管理员登陆日志")
public class LoginLog extends BaseEntity {

    @Column(columnDefinition = "varchar(30) comment '用户名'")
    private String userName;

    @Column(columnDefinition = "varchar(30) comment '昵称'")
    private String nickName;

    @Column(columnDefinition = "varchar(50) comment '内容'")
    private String content;


}
