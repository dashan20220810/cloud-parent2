package com.baisha.backendserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.UserBetStatisticsBusiness;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.vo.mq.gameServer.UserBetStatisticsVO;
import com.baisha.modulecommon.vo.mq.webServer.UserBetVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class DirectReceiver {

    @Autowired
    private UserBetStatisticsBusiness userBetStatisticsBusiness;

    /**
     * 用户下注统计
     *
     * @param jsonStr 投注金额 注单数
     */
    @RabbitListener(queues = MqConstants.USER_BET_STATISTICS)
    public void userBetStatistics(String jsonStr) {
        log.info("后台==用户下注统计===收到参数==={}", jsonStr);
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("用户下注统计 参数为空");
            return;
        }
        log.info("===================userBetStatistics start=====================================");
        UserBetVO userBetVO = JSONObject.parseObject(jsonStr, UserBetVO.class);
        if (Objects.isNull(userBetVO) || StringUtils.isEmpty(userBetVO.getBetTime())
                || null == userBetVO.getAmount() || null == userBetVO.getUserId()
                || StringUtils.isEmpty(userBetVO.getTgUserId())) {
            log.error("用户下注统计 参数不全");
            return;
        }
        userBetStatisticsBusiness.doUserBetStatistics(userBetVO);
        log.info("===================userBetStatistics end=====================================");
    }

    /**
     * 用户结算统计
     * 输赢金额
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.BACKEND_BET_SETTLEMENT_STATISTICS)
    public void userSettleBetStatistics(String jsonStr) {
        log.info("后台==用户结算统计===收到参数==={}", jsonStr);
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("用户结算统计 参数为空");
            return;
        }
        log.info("===================userSettleBetStatistics start=====================================");
        UserBetStatisticsVO userBetStatisticsVO = JSONObject.parseObject(jsonStr, UserBetStatisticsVO.class);
        if (Objects.isNull(userBetStatisticsVO) || StringUtils.isEmpty(userBetStatisticsVO.getBetTime())
                || null == userBetStatisticsVO.getWinAmount() || null == userBetStatisticsVO.getUserId()) {
            log.error("用户结算统计 参数不全");
            return;
        }
        userBetStatisticsBusiness.doUserSettleBetStatistics(userBetStatisticsVO);
        log.info("===================userSettleBetStatistics end=====================================");
    }


}
