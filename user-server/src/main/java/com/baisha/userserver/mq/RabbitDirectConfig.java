package com.baisha.userserver.mq;

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


    //重新开牌-结算 告诉user加回之前的打码量
    @Bean
    Binding userAddPlayMoneyAssetsBinding() {
        return BindingBuilder.bind(userAddPlayMoneyAssetsQueue()).to(gameAndUserDirectExchange())
                .with(MqConstants.USER_ADD_PLAYMONEY_ASSETS + "-direct");
    }

    @Bean
    Queue userAddPlayMoneyAssetsQueue() {
        return new Queue(MqConstants.USER_ADD_PLAYMONEY_ASSETS);
    }

}
