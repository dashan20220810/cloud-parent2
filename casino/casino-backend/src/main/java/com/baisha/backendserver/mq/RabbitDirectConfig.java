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

    @Bean
    DirectExchange baishaDirectExchange() {
        return new DirectExchange("baisha-direct", true, false);
    }


    @Bean
    Binding betSettlementBinding() {
        return BindingBuilder.bind(betSettlementQueue()).to(baishaDirectExchange())
                .with(MqConstants.BET_SETTLEMENT + "-direct");
    }

    /**
     * @return
     */
    @Bean
    Queue betSettlementQueue() {
        return new Queue(MqConstants.BET_SETTLEMENT);
    }


    //*****************************************gameServer - userServer *******************************************************************************
    @Bean
    DirectExchange gameAndUserDirectExchange() {
        return new DirectExchange("game-user-direct", true, false);
    }

    //通知用户中心  打码量和余额变化
    @Bean
    Binding userSettlementAssetsBinding() {
        return BindingBuilder.bind(userSettlementAssetsQueue()).to(gameAndUserDirectExchange())
                .with(MqConstants.USER_SETTLEMENT_ASSETS + "-direct");
    }

    @Bean
    Queue userSettlementAssetsQueue() {
        return new Queue(MqConstants.USER_SETTLEMENT_ASSETS);
    }
}
