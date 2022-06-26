package com.baisha.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "StartNewBureauVO对象", description = "开始新局-接收指令")
public class StartNewBureauVO {

    @ApiModelProperty(name = "TG群id", required = true)
    private String chatId;

    @ApiModelProperty(name = "机器人名称", required = true)
    private String username;

    @ApiModelProperty(name = "图片地址", required = true)
    private String imageAddress;

    @ApiModelProperty(name = "局号", required = true)
    private String bureauNum;

    @ApiModelProperty(name = "单注限红最低", required = true)
    private Integer minAmount;

    @ApiModelProperty(name = "单注限红最高", required = true)
    private Integer maxAmount;

    @ApiModelProperty(name = "当局最高", required = true)
    private Integer maxShoeAmount;
}
