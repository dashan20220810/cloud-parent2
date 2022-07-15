package com.baisha.backendserver.business;

import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.model.vo.award.BetResultRepairVO;
import com.baisha.modulecommon.MqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OpenAwardBusiness {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 告诉gameServer，这单要重开了
     *
     * @param repairVO
     */
    public void repairBetResult(BetResultRepairVO repairVO) {
        String s = JSONObject.toJSONString(repairVO);
        log.info("后台-补单-MQ消息：{}", s);
        rabbitTemplate.convertAndSend(MqConstants.GS_REPAIR_BET_RESULT, s);
    }


}
