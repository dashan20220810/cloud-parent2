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
@ApiModel(value = "StartNewBureauVO对象", description = "开始新局-倒计时")
public class StartNewBureauVO {

    @ApiModelProperty(value = "倒计时-视频地址", required = true)
    private String countdownAddress;

    @ApiModelProperty(value = "桌台ID", required = true)
    private Long tableId;

    @ApiModelProperty(value = "局号", required = true)
    private String bureauNum;

    // 校验参数合法性
    public static boolean check(StartNewBureauVO vo) throws IllegalAccessException {
       return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
