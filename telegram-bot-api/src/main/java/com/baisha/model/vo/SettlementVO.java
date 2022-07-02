package com.baisha.model.vo;

import com.baisha.modulecommon.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * @author kimi
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SettlementVO对象", description = "结算")
public class SettlementVO {

    @ApiModelProperty(value = "TG群结算信息", required = true)
    private Map<Long, List<UserWinVO>> settlementInfo;

    @ApiModelProperty(value = "局号", required = true)
    private String bureauNum;

    @ApiModelProperty(value = "结算结果", required = true)
    private String settlementResult;

    // 校验参数合法性
    public static boolean check(SettlementVO vo) throws IllegalAccessException {
       return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
