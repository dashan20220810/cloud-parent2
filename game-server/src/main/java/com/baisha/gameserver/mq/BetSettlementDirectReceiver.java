package com.baisha.gameserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.business.BetSettlementService;
import com.baisha.gameserver.model.Bet;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yihui
 */
@Component
@Slf4j
public class BetSettlementDirectReceiver {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private BetSettlementService betSettlementService;

    @RabbitListener(queues = MqConstants.BET_SETTLEMENT)
    public void betSettlement(BetSettleVO vo) {
        log.info("结算参数 {}", JSONObject.toJSONString(vo));
        //结算注单
        betSettlementService.betSettlement(vo);
        SettleFinishVO fvo = SettleFinishVO.builder().noActive(vo.getNoActive()).build();
        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, fvo);
    }
}
