package com.baisha.backendserver.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Slf4j
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "block_ip", comment = "角色")
@ApiModel(value = "后台-角色")
public class BlockIp  extends BaseEntity{

    @ApiModelProperty(value = "ip")
    @Column(unique = true, columnDefinition = "varchar(5) comment 'ip'")
    private String ip;

    @ApiModelProperty(value = "用戶名")
    @Column(unique = true, columnDefinition = "varchar(30) comment '用户名'")
    private String userName;

    @ApiModelProperty(value = "原因")
    @Column(unique = true, columnDefinition = "varchar(300) comment '原因'")
    private String reason;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    @Column(columnDefinition = "tinyint(2) comment '状态 1 正常 ，0禁用'")
    private Integer status;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
