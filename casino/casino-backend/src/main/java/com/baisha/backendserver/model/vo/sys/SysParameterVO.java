package com.baisha.backendserver.model.vo.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "后台-系统参数请求对象")
public class SysParameterVO {

    @ApiModelProperty(value = "返水比例(0-1数字 支持3位小数)", required = true)
    private BigDecimal rebate;


    public static boolean checkRebate(BigDecimal rebate) {
        if (null == rebate) {
            return true;
        }
        if (rebate.compareTo(BigDecimal.ZERO) < 0) {
            return true;
        }
        BigDecimal ge = new BigDecimal("1");
        if (rebate.compareTo(ge) > 0) {
            return true;
        }

        return false;
    }


}
