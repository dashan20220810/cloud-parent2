package com.baisha.backendserver.model.vo.order;


import com.baisha.backendserver.model.vo.PageVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yihui
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "下注分页对象")
public class BetPageVO extends PageVO {

    @ApiModelProperty("会员名")
    private String userName;

    @ApiModelProperty("下注类型  Z（庄） X（闲） H（和） ZD（庄对） XD（闲对） D（庄对 闲对）SB（庄对 闲对 和）SS（幸运6）")
    private String betOption;

    @ApiModelProperty("游戏轮号")
    private String noRun;

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty("注單狀態(1.下注成功)")
    private Integer status;

}
