package com.baisha.backendserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Entity;

@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "role", comment = "角色")
@ApiModel(value = "后台-角色")
public class Role extends BaseEntity{

    @ApiModelProperty(value = "代碼")
    @Column(unique = true, columnDefinition = "varchar(5) comment '代碼'")
    private String code;

    @ApiModelProperty(value = "角色名稱")
    @Column(unique = true, columnDefinition = "varchar(30) comment '名稱'")
    private String name;

    @ApiModelProperty(value = "狀態")
    @Column(unique = true, columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status;

}
