package com.baisha.casinoweb.model.bo;

import java.io.Serializable;

import javax.persistence.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@ApiModel(value = "web-游戏玩法赔率对象", description = "游戏玩法赔率")
public class BsOddsBO implements Serializable {


    private static final long serialVersionUID = -8854003236614950837L;

    @ApiModelProperty(value = "玩法编码")
    private String ruleCode;

    @ApiModelProperty(value = "最大限红")
    private Integer maxAmount;

    @ApiModelProperty(value = "最小限红")
    private Integer minAmount;
}
