package com.baisha.modulecommon.vo.mq.gameServer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RepairBetResultVO {

    //"类型: ZD庄对,XD闲对,Z庄,X闲,H和,(SS2,SS3)幸运六  多个 英文,隔开"
    private String awardOption;

    //"游戏局号"
    private String noActive;

}
