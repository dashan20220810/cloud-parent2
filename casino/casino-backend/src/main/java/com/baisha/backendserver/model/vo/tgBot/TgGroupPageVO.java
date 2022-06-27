package com.baisha.backendserver.model.vo.tgBot;

import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yihui
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "后台-获取机器人的电报群请求对象")
public class TgGroupPageVO extends PageVO {

    @ApiModelProperty(value = "机器人Id", required = true)
    private String id;

}
