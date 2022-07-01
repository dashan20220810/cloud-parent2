package com.baisha.gameserver.mq;

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
        		.with(MqConstants.BET_SETTLEMENT +"-direct");
    }
    
    /**
     *
     * @return
     */
    @Bean
    Queue betSettlementQueue() {
        return new Queue(MqConstants.BET_SETTLEMENT);
    }

    @Bean
    Binding settlementFinishBinding() {
        return BindingBuilder.bind(settlementFinishQueue()).to(baishaDirectExchange())
        		.with(MqConstants.SETTLEMENT_FINISH +"-direct");
    }

    @Bean
    Queue settlementFinishQueue() {
        return new Queue(MqConstants.SETTLEMENT_FINISH);
    }
}
