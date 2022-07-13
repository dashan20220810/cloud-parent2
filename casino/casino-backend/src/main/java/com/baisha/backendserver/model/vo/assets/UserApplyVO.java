package com.baisha.backendserver.model.vo.assets;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yihui
 */
@Data
@ApiModel(value = "后台-会员人工添加额度申请请求对象")
public class UserApplyVO {

    @ApiModelProperty(value = "TG用户ID", required = true)
    private String tgUserId;

    @ApiModelProperty(value = "会员ID", required = true)
    private Long userId;

    @ApiModelProperty(value = "调整金额 大于0数字", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "流水倍数 >=0 整数(1-3位)", required = true)
    private Integer flowMultiple;

    @ApiModelProperty(value = "调整类型", required = true)
    private Integer adjustmentType;

    @ApiModelProperty(value = "原因(1-100位)")
    private String remark;

    @ApiModelProperty(value = "附件key")
    private String fileKey;


}
