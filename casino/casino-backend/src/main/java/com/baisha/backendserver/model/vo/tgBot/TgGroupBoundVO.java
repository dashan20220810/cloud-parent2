package com.baisha.backendserver.model.vo.tgBot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-电报群限红")
public class TgGroupBoundVO {

    @ApiModelProperty(value = "TG群ID", required = true)
    private String tgGroupId;

    //限红：单注20-15000  当局最高50000（美金)
    @ApiModelProperty(value = "单注最低", required = true)
    private Integer minAmount;

    @ApiModelProperty(value = "单注最高", required = true)
    private Integer maxAmount;

    @ApiModelProperty(value = "当局最高", required = true)
    private Integer maxShoeAmount;

}
