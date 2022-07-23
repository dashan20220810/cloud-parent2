package com.baisha.backendserver.model.vo.assets;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-TG用户请求对象")
public class TgUserVO {

    @ApiModelProperty(value = "TG用户ID", required = true)
    private String tgUserId;

}
