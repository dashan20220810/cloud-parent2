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
@ApiModel(value = "OpenCardVO对象", description = "开牌")
public class OpenCardVO {

    @ApiModelProperty(value = "开牌-图片地址")
    private String openCardAddress;

    @ApiModelProperty(value = "实时开牌俯视地址")
    private String lookDownAddress = "";

    @ApiModelProperty(value = "实时开牌正面地址")
    private String frontAddress = "";

    @ApiModelProperty(value = "牌面-视频地址")
    private String videoResultAddress;

    @ApiModelProperty(value = "路图-图片地址")
    private String picRoadAddress;

    @ApiModelProperty(value = "桌子id", required = true)
    private Long tableId;

    // 校验参数合法性
    public static boolean check(OpenCardVO vo) throws IllegalAccessException {
       return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
