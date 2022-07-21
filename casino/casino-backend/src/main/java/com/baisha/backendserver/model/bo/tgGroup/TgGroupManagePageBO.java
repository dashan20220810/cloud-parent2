package com.baisha.backendserver.model.bo.tgGroup;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-TG群分页管理对象")
public class TgGroupManagePageBO extends BaseBO {

    @ApiModelProperty(value = "TG群ID")
    private String chatId;

    @ApiModelProperty(value = "TG群名称")
    private String chatName;

    @ApiModelProperty(value = "状态")
    private Integer status;


 /*"chatId": -1001789896595,
         "botId": 1,
         "tableId": 1,
         "chatName": "开发环境-TG群",
         "botName": "asha_dev_bot",
         "status": 1,
*/


}
