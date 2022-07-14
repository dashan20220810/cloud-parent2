package com.baisha.backendserver.model.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "后台-新增管理员请求对象")
public class AdminUpdateVO extends AdminAddVO{

    @ApiModelProperty(value = "账号id", required = true)
    private Long id;
}
