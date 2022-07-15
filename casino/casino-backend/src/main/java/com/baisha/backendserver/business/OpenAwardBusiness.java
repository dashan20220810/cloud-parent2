package com.baisha.backendserver.business;

import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.model.vo.award.BetResultReopenVO;
import com.baisha.backendserver.model.vo.award.BetResultRepairVO;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.gameServer.ReopenBetResultVO;
import com.baisha.modulecommon.vo.mq.gameServer.RepairBetResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenAwardBusiness {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 告诉gameServer，这局要补
     * 补单
     *
     * @param repairVO
     */
    public void repairBetResult(BetResultRepairVO repairVO) {
        RepairBetResultVO vo = RepairBetResultVO.builder().noActive(repairVO.getNoActive()).awardOption(repairVO.getAwardOption()).build();
        String s = JSONObject.toJSONString(vo);
        log.info("后台-补单-MQ消息：{}", s);
        rabbitTemplate.convertAndSend(MqConstants.GS_REPAIR_BET_RESULT, s);
    }

    /**
     * 告诉gameServer，这局要重开了
     * 重新开牌
     *
     * @param reopenVO
     */
    public void reopenBetResult(BetResultReopenVO reopenVO) {
        ReopenBetResultVO vo = ReopenBetResultVO.builder().noActive(reopenVO.getNoActive()).awardOption(reopenVO.getAwardOption()).build();
        String s = JSONObject.toJSONString(vo);
        log.info("后台-重新开牌-MQ消息：{}", s);
        rabbitTemplate.convertAndSend(MqConstants.GS_REOPEN_BET_RESULT, s);
    }
}
