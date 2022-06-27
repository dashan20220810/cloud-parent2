package com.baisha.model.vo;

import com.baisha.modulecommon.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "AuditVo", description = "审核参数")
public class AuditVo {

    private Long chatId;
    private Integer state;
    private Long tableId;
    private Integer minAmount;
    private Integer maxAmount;
    private Integer maxShoeAmount;

    public static boolean check(AuditVo vo) throws IllegalAccessException {
        boolean b = CommonUtil.checkObjectFieldNotNull(vo);
        if (!b) {
            return false;
        }

        if (vo.getChatId() < 0 || vo.getTableId() < 0 || vo.getMinAmount() < 0 || vo.getMaxAmount() < 0 || vo.getMaxShoeAmount() < 0) {
            return false;
        }

        return true;
    }
}
