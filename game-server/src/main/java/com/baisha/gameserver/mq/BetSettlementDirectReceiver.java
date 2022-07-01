package com.baisha.gameserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yihui
 */
@Component
public class BetSettlementDirectReceiver {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = MqConstants.BET_SETTLEMENT)
    public void betSettlement(BetSettleVO vo) {
        System.out.println("==============" + JSONObject.toJSONString(vo));
        SettleFinishVO fvo = SettleFinishVO.builder().noActive(vo.getNoActive()).build();
        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, fvo);
    }
}
