package com.baisha.casinoweb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @Author 小智
 * @Date 14/7/22 5:00 PM
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "open_card_video", indexes = {@Index(columnList = "no_active")})
@ApiModel(value = "开牌视频录单图存储对象", description = "开牌视频录单图存储对象")
public class OpenCardVideo extends BaseEntity{

    private static final long serialVersionUID = 4255454883070031377L;

    @ApiModelProperty("游戏局号")
    @Column(name = "no_active", nullable = false, columnDefinition = "VARCHAR(50) COMMENT '游戏局号'")
    private String noActive;

    @ApiModelProperty("开牌视频流地址")
    @Column(name = "video_address", nullable = false, columnDefinition = "VARCHAR(200) COMMENT '开牌视频流地址'")
    private String videoAddress;

    @ApiModelProperty("录单图地址")
    @Column(name = "pic_address", nullable = false, columnDefinition = "VARCHAR(1000) COMMENT '录单图地址'")
    private String picAddress;
}
