package com.baisha.backendserver.mq;

import com.baisha.modulecommon.MqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DirectReceiver {

    /**
     * 用户下注统计
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.USER_BET_STATISTICS)
    public void userBetStatistics(String jsonStr) {
        log.info("=====参数==={}", jsonStr);

    }

    /**
     * 用户结算统计
     *
     * @param jsonStr
     */
    public void userSettleBetStatistics(String jsonStr) {
        log.info("=====参数==={}", jsonStr);

    }


}
