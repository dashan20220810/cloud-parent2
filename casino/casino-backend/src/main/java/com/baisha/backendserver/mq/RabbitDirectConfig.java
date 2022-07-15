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

    @Bean
    Binding userBetStatisticsBinding() {
        return BindingBuilder.bind(userBetStatisticsQueue()).to(webAndBackendDirectExchange())
                .with(MqConstants.USER_BET_STATISTICS + "-direct");
    }

    @Bean
    Queue userBetStatisticsQueue() {
        return new Queue(MqConstants.USER_BET_STATISTICS);
    }


    //*****************************************gameServer - adminServer *******************************************************************************
    @Bean
    DirectExchange gameAndBackendDirectExchange() {
        return new DirectExchange("game-backend-direct", true, false);
    }

    //通知后台 统计用户注单
    @Bean
    Binding userBackendBetStatisticsBinding() {
        return BindingBuilder.bind(userBackendBetStatisticsQueue()).to(gameAndBackendDirectExchange())
                .with(MqConstants.BACKEND_BET_SETTLEMENT_STATISTICS + "-direct");
    }

    @Bean
    Queue userBackendBetStatisticsQueue() {
        return new Queue(MqConstants.BACKEND_BET_SETTLEMENT_STATISTICS);
    }


    //通知gs 补单
    @Bean
    Binding gsRepairBetResultBinding() {
        return BindingBuilder.bind(gsRepairBetResultQueue()).to(gameAndBackendDirectExchange())
                .with(MqConstants.GS_REPAIR_BET_RESULT + "-direct");
    }

    @Bean
    Queue gsRepairBetResultQueue() {
        return new Queue(MqConstants.GS_REPAIR_BET_RESULT);
    }

    //通知gs 重新开牌
    @Bean
    Binding gsReopenBetResultBinding() {
        return BindingBuilder.bind(gsRepairBetResultQueue()).to(gameAndBackendDirectExchange())
                .with(MqConstants.GS_REOPEN_BET_RESULT + "-direct");
    }

    @Bean
    Queue gsReopenBetResultQueue() {
        return new Queue(MqConstants.GS_REOPEN_BET_RESULT);
    }


}
