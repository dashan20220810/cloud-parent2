package com.baisha.modulecommon.vo.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 结算完成
 * @author yihui
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SettleFinishVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4470186284001035447L;
    /**
     * 局号
     */
    private String noActive;

    /**
     * 开牌结果
     */
    private String openCardResult;


}
