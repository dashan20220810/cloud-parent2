package com.baisha.modulecommon.vo.mq.tgBotServer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 18/7/22 7:44 PM
 * @Version 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BotGroupVO implements Serializable {

    /** 花名s */
    private String tgUserName;
}
