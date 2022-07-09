package com.baisha.gameserver.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "gs-id对象请求对象")
public class IdVO {

    @ApiModelProperty(value = "ID",required = true)
    private Long id;

}
