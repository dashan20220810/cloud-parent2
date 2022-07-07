package com.baisha.gameserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author yihui
 */
@Data
@Entity
@org.hibernate.annotations.Table(appliesTo = "bs_game", comment = "游戏")
@ApiModel(value = "游戏对象", description = "游戏")
public class BsGame extends BaseEntity {

    @ApiModelProperty(value = "游戏编码")
    @Column(unique = true, columnDefinition = "varchar(20) comment '游戏编码'")
    private String gameCode;

    @ApiModelProperty(value = "游戏名称")
    @Column(columnDefinition = "varchar(20) comment '游戏名称'")
    private String gameName;


}
