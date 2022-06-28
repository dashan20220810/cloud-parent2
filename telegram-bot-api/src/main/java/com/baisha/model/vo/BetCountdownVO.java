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
@ApiModel(value = "BetCountdownVO对象", description = "投注倒计时-接收指令")
public class BetCountdownVO {

    @ApiModelProperty(value = "图片地址", required = true)
    private String imageAddress;

    @ApiModelProperty(value = "桌台ID", required = true)
    private Long tableId;

    // 校验参数合法性
    public static boolean check(BetCountdownVO vo) throws IllegalAccessException {
       return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
