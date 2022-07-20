package com.baisha.backendserver.model.bo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author yihui
 */
@Builder
@Data
@ApiModel(value = "后台-改变类型和名称返回对象")
public class ChangeTypeNameBO {

    @ApiModelProperty(value = "类型")
    Integer changeType;

    @ApiModelProperty(value = "名称")
    String changeTypeName;
}
