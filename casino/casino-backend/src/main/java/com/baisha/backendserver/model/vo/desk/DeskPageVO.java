package com.baisha.backendserver.model.vo.desk;

import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-桌台分页请求对象")
public class DeskPageVO extends PageVO {
    @ApiModelProperty(value = "桌台编码(1-10位 例如G26)")
    private String deskCode;

    @ApiModelProperty(value = "桌台名称(1-30位 例如 百家乐一台)")
    private String deskName;

    @ApiModelProperty(value = "内网IP(1-20位)")
    private String localIp;

    @ApiModelProperty(value = "状态 1 正常 ，0禁用")
    private Integer status;

    @ApiModelProperty(value = "游戏编码")
    private String gameCode;
}
