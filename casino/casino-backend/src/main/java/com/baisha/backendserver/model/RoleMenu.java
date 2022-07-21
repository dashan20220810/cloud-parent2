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
@org.hibernate.annotations.Table(appliesTo = "role_menu", comment = "角色功能對應表")
@ApiModel(value = "后台-角色功能對應表")
public class RoleMenu extends BaseEntity{

    @ApiModelProperty(value = "角色ID")
    @Column(unique = true, columnDefinition = "bigint comment '角色ID'")
    private Long roleId;

    @ApiModelProperty(value = "菜单ID")
    @Column(unique = true, columnDefinition = "bigint comment '菜单ID'")
    private Long menuId;

}
