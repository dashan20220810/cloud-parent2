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
public class BetSettleVO implements Serializable {

    /**
     * 局号
     */
    private String noActive;


    /**
     * 开奖选择 字母
     * Z（庄）  X（闲）  H（和） ZD（庄对） XD（闲对） D（庄对 闲对）SB（庄对 闲对 和）SS（幸运6）
     */
    private String awardOption;


}
