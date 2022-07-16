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

    //重新开牌-结算 告诉user扣除之前的金额
    @Bean
    Binding userSubtractAssetsBinding() {
        return BindingBuilder.bind(userSubtractAssetsQueue()).to(gameAndUserDirectExchange())
                .with(MqConstants.USER_SUBTRACT_ASSETS + "-direct");
    }

    @Bean
    Queue userSubtractAssetsQueue() {
        return new Queue(MqConstants.USER_SUBTRACT_ASSETS);
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
