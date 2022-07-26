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
@org.hibernate.annotations.Table(appliesTo = "menu", comment = "菜單功能")
@ApiModel(value = "后台-菜單功能")
public class Menu extends BaseEntity{

    @ApiModelProperty(value = "代碼")
    @Column(unique = true, columnDefinition = "varchar(20) comment '代碼'")
    private String code;

    @ApiModelProperty(value = "菜單或功能名稱")
    @Column(unique = true, columnDefinition = "varchar(30) comment '菜單或功能名稱'")
    private String name;

    @ApiModelProperty(value = "上層節點id")
    @Column(columnDefinition = "bigint comment '上層節點id'")
    private Long parentId;

    @ApiModelProperty(value = "類型")
    @Column(columnDefinition = "tinyint comment '節點類型  1 一級菜單  2 二級菜單 3 功能按鈕'")
    private Integer type;

    @ApiModelProperty(value = "api url")
    @Column(columnDefinition = "varchar(100) comment '功能對應的api url '")
    private Integer apiUrl;

    @ApiModelProperty(value = "层次")
    @Column(columnDefinition = "tinyint comment '层次'")
    private Integer level;

    @ApiModelProperty(value = "树id的路径")
    @Column(columnDefinition = "varchar(200) comment '树id的路径 整个层次上的路径id，逗号分隔'")
    private String path;

    @ApiModelProperty(value = "狀態")
    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status;

}
