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
    Queue settleFinish() {
        return new Queue(MqConstants.SETTLEMENT_FINISH);
    }

    @Bean
    DirectExchange settleFinishDirectExchange() {
        return new DirectExchange("settleFinish-direct", true, false);
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(settleFinish()).to(settleFinishDirectExchange()).with("direct");
    }
}
