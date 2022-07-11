package com.baisha.gameserver.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yihui
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@org.hibernate.annotations.Table(appliesTo = "bs_game", comment = "游戏")
@ApiModel(value = "游戏对象", description = "游戏")
public class BsGame extends BaseEntity {

    private static final long serialVersionUID = 2155650561486945489L;

	@ApiModelProperty(value = "游戏编码")
    @Column(unique = true, columnDefinition = "varchar(20) comment '游戏编码'")
    private String gameCode;

    @ApiModelProperty(value = "游戏名称")
    @Column(columnDefinition = "varchar(20) comment '游戏名称'")
    private String gameName;


}
