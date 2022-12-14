package com.baisha.backendserver.model.bo.award;

import com.baisha.backendserver.model.bo.BaseBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yihui
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BetResultBO extends BaseBO {

    @ApiModelProperty("table_id")
    private Long tableId;

    @ApiModelProperty("游戏局号")
    private String noActive;

    @ApiModelProperty("类型: ZD庄对,XD闲对,Z庄,X闲,H和,SS2,SS3幸运六")
    private String awardOption;

    @ApiModelProperty("类型: ZD庄对,XD闲对,Z庄,X闲,H和,SS2,SS3幸运六")
    private String awardOptionName;

    @ApiModelProperty("重开牌(0.否 1.是)")
    private Integer reopen = 0;

}
