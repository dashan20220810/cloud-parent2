package com.baisha.backendserver.model.bo.assets;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yihui
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value = "后台-调整类型")
public class OrderAdjustmentTypeBO {

    @ApiModelProperty(value = "类型")
    private Integer code;

    @ApiModelProperty(value = "名称")
    private String name;


}
