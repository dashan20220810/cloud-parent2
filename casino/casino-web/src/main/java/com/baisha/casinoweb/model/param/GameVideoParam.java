package com.baisha.casinoweb.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author 小智
 * @Date 7/7/22 3:48 PM
 * @Version 1.0
 */
@Data
@Slf4j
@ApiModel(value = "游戏视频流请求对象")
public class GameVideoParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 5385040263485447780L;

    @ApiModelProperty(required=true, value="荷官触发时间")
    private Long qtime;

    @ApiModelProperty(required=true, value="局号")
    private String period;

    @ApiModelProperty(required=true, value="近景视频流")
    private String rtmpurl;
}
