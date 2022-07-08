package com.baisha.gameserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.business.BetSettlementBusiness;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yihui
 */
@Component
@Slf4j
public class BetSettlementDirectReceiver {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private BetSettlementBusiness betSettlementService;

    @RabbitListener(queues = MqConstants.BET_SETTLEMENT)
    public void betSettlement(BetSettleVO vo) {
        sleep();
        log.info("结算参数 {}", JSONObject.toJSONString(vo));
        //结算注单
        betSettlementService.betSettlement(vo);
        SettleFinishVO fvo = SettleFinishVO.builder().noActive(vo.getNoActive()).build();
        rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, fvo);
    }

    public void sleep() {
        try {
            //收到结算通知后，延迟X秒，防止有人在封盘的瞬间下注 而造成 注单未查询到 造成的不能结算的情况
            Thread.sleep(2000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
