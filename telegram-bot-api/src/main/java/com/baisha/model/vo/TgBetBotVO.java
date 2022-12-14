package com.baisha.model.vo;

import com.baisha.modulecommon.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TgBetBotVO对象", description = "TG下注机器人VO")
public class TgBetBotVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "机器人id")
    private String betBotId;

    @ApiModelProperty(value = "机器人名称", required = true)
    private String betBotName;

    @ApiModelProperty(value = "机器人手机号")
    private String betBotPhone;

    @ApiModelProperty(value = "投注开始时间", required = true)
    private String betStartTime;

    @ApiModelProperty(value = "投注结束时间", required = true)
    private String betEndTime;

    @ApiModelProperty(value = "投注频率", required = true)
    private Integer betFrequency;

    @ApiModelProperty(value = "投注内容", required = true)
    private String betContents;

    @ApiModelProperty(value = "投注金额-最小倍数", required = true)
    private Integer minMultiple;

    @ApiModelProperty(value = "投注金额-最大倍数", required = true)
    private Integer maxMultiple;

    // 校验参数合法性
    public static boolean check(TgBetBotVO vo) throws IllegalAccessException {
        return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
