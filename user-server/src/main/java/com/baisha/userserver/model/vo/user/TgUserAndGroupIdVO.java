package com.baisha.userserver.model.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "Tg用户ID和群ID对象")
public class TgUserAndGroupIdVO {

    @ApiModelProperty(value = "TG用户ID", required = true)
    private String tgUserId;

    @ApiModelProperty(value = "TG群ID", required = true)
    private String tgGroupId;


}
