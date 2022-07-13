package com.baisha.casinoweb.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baisha.modulecommon.MqConstants;

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

    @Bean
    Binding openNewGameBinding() {
        return BindingBuilder.bind(settlementFinishQueue()).to(baishaDirectExchange())
        		.with(MqConstants.WEB_OPEN_NEW_GAME +"-direct");
    }

    @Bean
    Queue openNewGameQueue() {
        return new Queue(MqConstants.WEB_OPEN_NEW_GAME);
    }

    @Bean
    Binding openBinding() {
        return BindingBuilder.bind(settlementFinishQueue()).to(baishaDirectExchange())
        		.with(MqConstants.WEB_CLOSE_GAME +"-direct");
    }

    @Bean
    Queue openQueue() {
        return new Queue(MqConstants.WEB_CLOSE_GAME);
    }


    //*****************************************gameServer - userServer *******************************************************************************
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
