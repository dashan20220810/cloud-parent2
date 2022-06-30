package com.baisha.model.vo;

import com.baisha.modulecommon.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SealingLineVO对象", description = "封盘线")
public class SealingLineVO {

    @ApiModelProperty(value = "TG群下注信息", required = true)
    private Map<Long, BetUserAmountVO> tgBetInfo;

    @ApiModelProperty(value = "局号", required = true)
    private String bureauNum;

    // 校验参数合法性
    public static boolean check(SealingLineVO vo) throws IllegalAccessException {
       return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
