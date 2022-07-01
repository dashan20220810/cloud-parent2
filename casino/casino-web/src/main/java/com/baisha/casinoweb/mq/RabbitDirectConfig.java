package com.baisha.casinoweb.mq;

import com.baisha.modulecommon.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitDirectConfig {

    @Bean
    Queue queue() {
        return new Queue("hello-queue");
    }


    @Bean
    DirectExchange directExchange() {
        return new DirectExchange("first-direct", true, false);
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with("direct");
    }


    /**
     * 下注结算
     *
     * @return
     */
    @Bean
    Queue betSettlementQueue() {
        return new Queue(MqConstants.BET_SETTLEMENT);
    }

    @Bean
    DirectExchange betSettlementDirectExchange() {
        return new DirectExchange("betSettlement-direct", true, false);
    }

    @Bean
    Binding betSettlementBinding() {
        return BindingBuilder.bind(betSettlementQueue()).to(betSettlementDirectExchange()).with("direct");
    }
}
