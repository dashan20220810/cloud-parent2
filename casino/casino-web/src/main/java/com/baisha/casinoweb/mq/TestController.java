package com.baisha.casinoweb.mq;

import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("hello")
    @NoAuthentication
    public void hello() {
        rabbitTemplate.convertAndSend("hello-queue", "hello direct!");
    }

    @GetMapping("betSettlement")
    @NoAuthentication
    public void betSettlement() {
        BetSettleVO vo = BetSettleVO.builder().noActive("G01202206301004").awardOption("X").build();
        rabbitTemplate.convertAndSend(MqConstants.BET_SETTLEMENT, vo);
    }

}
