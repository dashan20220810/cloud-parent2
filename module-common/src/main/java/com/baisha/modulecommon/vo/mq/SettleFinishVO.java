package com.baisha.modulecommon.vo.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /**
     * 局号
     */
    private String noActive;


}
