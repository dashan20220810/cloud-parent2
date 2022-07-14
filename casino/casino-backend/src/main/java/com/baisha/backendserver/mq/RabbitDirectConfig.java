package com.baisha.backendserver.mq;

import com.baisha.modulecommon.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yihui
 */
@Configuration
public class RabbitDirectConfig {

    //*****************************************webServer - adminServer *******************************************************************************
    @Bean
    DirectExchange webAndBackendDirectExchange() {
        return new DirectExchange("web-backend-direct", true, false);
    }

    //通知用户中心  打码量和余额变化
    @Bean
    Binding userBetStatisticsBinding() {
        return BindingBuilder.bind(userBetStatisticsQueue()).to(webAndBackendDirectExchange())
                .with(MqConstants.USER_BET_STATISTICS + "-direct");
    }

    @Bean
    Queue userBetStatisticsQueue() {
        return new Queue(MqConstants.USER_BET_STATISTICS);
    }
}
