package com.baisha.modulecommon.vo.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 下注结算
 * @author yihui
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SettleFinishVO implements Serializable {

    /**
     * 桌台IP
     */
    private String dealerIp;

    /**
     * 荷官端局号
     */
    private String gameNo;


    /**
     * 开奖选择 字母
     * Z（庄）  X（闲）  H（和） ZD（庄对） XD（闲对） SS（幸运6）
     */
    private String consequences;

}
