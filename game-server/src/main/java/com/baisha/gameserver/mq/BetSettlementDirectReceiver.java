package com.baisha.gameserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.business.BetSettlementBusiness;
import com.baisha.gameserver.util.contants.RedisConstants;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author yihui
 */
@Component
@Slf4j
public class BetSettlementDirectReceiver {
    @Autowired
    private RedissonClient redisson;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private BetSettlementBusiness betSettlementService;

    @RabbitListener(queues = MqConstants.BET_SETTLEMENT)
    public void betSettlement(BetSettleVO vo) {
        log.info("结算参数 {}", JSONObject.toJSONString(vo));
        //使用当前局号  使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.GAMESERVER_SETTLEMENT + vo.getNoActive());
        try {
            boolean res = fairLock.tryLock(RedisConstants.SETTLEMENT_WAIT_TIME, RedisConstants.SETTLEMENT_UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //结算注单
                betSettlementService.betSettlement(vo);

                //结算完毕 通知
                BetSettleVO fvo = BetSettleVO.builder().noActive(vo.getNoActive()).awardOption(vo.getAwardOption()).build();
                rabbitTemplate.convertAndSend(MqConstants.SETTLEMENT_FINISH, fvo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            fairLock.unlock();
        }
    }


}
