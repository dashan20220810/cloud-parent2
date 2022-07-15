package com.baisha.backendserver.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@ApiModel(value = "后台-编码和名称返回对象")
public class CodeNameBO {

    @ApiModelProperty(value = "编码")
    String code;

    @ApiModelProperty(value = "名称")
    String name;

}
