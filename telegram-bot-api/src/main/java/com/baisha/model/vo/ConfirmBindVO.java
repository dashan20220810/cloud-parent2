package com.baisha.model.vo;

import com.baisha.modulecommon.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author kimi
 */
@Data
@ApiModel(value = "TG群-下注机器人-绑定")
public class ConfirmBindVO {

    @ApiModelProperty(value = "TgChat对象-主键", required = true)
    private Long tgChatId;

    @ApiModelProperty(value = "TgBetBot对象-主键-集合", required = true)
    private String tgBetBotIds;

    // 校验参数合法性
    public static boolean check(ConfirmBindVO vo) throws IllegalAccessException {
        return CommonUtil.checkObjectFieldNotNull(vo);
    }
}
