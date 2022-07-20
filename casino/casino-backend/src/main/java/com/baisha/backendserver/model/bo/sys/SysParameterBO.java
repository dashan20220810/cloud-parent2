package com.baisha.backendserver.model.bo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "后台-系统参数返回对象")
public class SysParameterBO {

    @ApiModelProperty(value = "返水比例(0-1数字 支持3位小数)")
    private BigDecimal rebate;


}
