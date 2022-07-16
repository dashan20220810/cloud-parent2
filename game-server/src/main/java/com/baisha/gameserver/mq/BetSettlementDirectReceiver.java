package com.baisha.gameserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.business.BetSettlementBusiness;
import com.baisha.gameserver.model.BetResult;
import com.baisha.gameserver.model.BetResultChange;
import com.baisha.gameserver.service.BetResultChangeService;
import com.baisha.gameserver.service.BetResultService;
import com.baisha.gameserver.util.contants.RedisConstants;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulecommon.vo.mq.gameServer.ReopenBetResultVO;
import com.baisha.modulecommon.vo.mq.gameServer.RepairBetResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
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
    private BetResultService betResultService;
    @Autowired
    private BetResultChangeService betResultChangeService;
    @Autowired
    private BetSettlementBusiness betSettlementService;

    @RabbitListener(queues = MqConstants.BET_SETTLEMENT)
    public void betSettlement(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("结算参数不能为空");
            return;
        }
        BetSettleVO vo = JSONObject.parseObject(jsonStr, BetSettleVO.class);
        log.info("结算参数 {}", JSONObject.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getNoActive()) || StringUtils.isEmpty(vo.getAwardOption())) {
            log.error("结算参数不全jsonStr={}", jsonStr);
            return;
        }
        //使用当前局号  使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.GAMESERVER_SETTLEMENT + vo.getNoActive());
        try {
            boolean res = fairLock.tryLock(RedisConstants.SETTLEMENT_WAIT_TIME, RedisConstants.SETTLEMENT_UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //结算注单
                betSettlementService.betSettlement(vo);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            fairLock.unlock();
        }
    }


    /**
     * 补单-结算
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.GS_REPAIR_BET_RESULT)
    public void repairBetResult(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("补单-结算参数不能为空");
            return;
        }
        RepairBetResultVO vo = JSONObject.parseObject(jsonStr, RepairBetResultVO.class);
        log.info("补单-结算参数 {}", JSONObject.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getNoActive()) || StringUtils.isEmpty(vo.getAwardOption())) {
            log.error("补单-结算参数不全jsonStr={}", jsonStr);
            return;
        }
        //使用当前局号  使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.GAMESERVER_SETTLEMENT + vo.getNoActive());
        try {
            boolean res = fairLock.tryLock(RedisConstants.SETTLEMENT_WAIT_TIME, RedisConstants.SETTLEMENT_UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //结算注单
                betSettlementService.betSettlement(BetSettleVO.builder()
                        .noActive(vo.getNoActive()).awardOption(vo.getAwardOption()).build());
                //写记录
                BetResult result = betResultService.findByNoActive(vo.getNoActive());
                doBetResultChange(result, vo.getAwardOption());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            fairLock.unlock();
        }
    }

    private void doBetResultChange(BetResult result, String awardOption) {
        if (Objects.nonNull(result)) {
            BetResultChange change = new BetResultChange();
            change.setTableId(result.getTableId());
            change.setNoActive(result.getNoActive());
            change.setAwardOption(result.getAwardOption());
            change.setFinalAwardOption(awardOption);
            betResultChangeService.save(change);
        }
    }


    /**
     * 重新开牌-结算
     * 先减去已经派过彩(返水+中奖)，然后在派奖
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.GS_REOPEN_BET_RESULT)
    public void reopenBetResult(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("重新开牌-结算参数不能为空");
            return;
        }
        ReopenBetResultVO vo = JSONObject.parseObject(jsonStr, ReopenBetResultVO.class);
        log.info("重新开牌-结算参数 {}", JSONObject.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getNoActive()) || StringUtils.isEmpty(vo.getAwardOption())) {
            log.error("重新开牌-结算参数不全jsonStr={}", jsonStr);
            return;
        }
        //使用当前局号  使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.GAMESERVER_SETTLEMENT + vo.getNoActive());
        try {
            boolean res = fairLock.tryLock(RedisConstants.SETTLEMENT_WAIT_TIME, RedisConstants.SETTLEMENT_UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //重新开牌-结算注单
                betSettlementService.betReopenSettlement(BetSettleVO.builder()
                        .noActive(vo.getNoActive()).awardOption(vo.getAwardOption()).build());
                //写记录
                BetResult result = betResultService.findByNoActive(vo.getNoActive());
                doBetResultChange(result, vo.getAwardOption());
                //修改开奖结果 reopen
                doUpdateBetResult(result, vo.getAwardOption());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            fairLock.unlock();
        }
    }

    private void doUpdateBetResult(BetResult result, String awardOption) {
        if (Objects.nonNull(result)) {
            betResultService.updateReopenAndAwardOptionByNoActive(result.getNoActive(), awardOption);
        }
    }


}
