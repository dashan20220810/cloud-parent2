package com.baisha.backendserver.model;

import lombok.Builder;
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
@org.hibernate.annotations.Table(appliesTo = "operate_log", comment = "操作日志")
public class OperateLog extends BaseEntity {

    @Column(unique = true, columnDefinition = "varchar(30) comment '用户名'")
    private String userName;

    @Column(columnDefinition = "varchar(30) comment '昵称'")
    private String nickName;

    @Column(columnDefinition = "varchar(30) comment '模块名'")
    private String moduleName;

    @Column(columnDefinition = "varchar(10) comment '操作类型'")
    private String activeType;

    @Column(columnDefinition = "text comment '操作内容'")
    private String content;


}
