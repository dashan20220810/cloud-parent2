package com.baisha.backendserver.model.vo.ipWhiteList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "后台-新增ip白名單请求对象")
public class IpwhiteListVO {

    @ApiModelProperty(value = "名稱(長度30內)", required = true)
    private String name;

    @ApiModelProperty(value = "描述或說明(長度100內)", required = true)
    private String description;

    @ApiModelProperty(value = "白名單ip(符合規範的ip4)", required = true)
    private String ip;

    @ApiModelProperty(value = "狀態(開啟為1,關閉為2)", required = true)
    private Integer status;

}
