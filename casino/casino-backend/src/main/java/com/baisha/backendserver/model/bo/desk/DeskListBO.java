package com.baisha.backendserver.model.bo.desk;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-桌台列表")
public class DeskListBO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "桌台编码")
    private String deskCode;

}
