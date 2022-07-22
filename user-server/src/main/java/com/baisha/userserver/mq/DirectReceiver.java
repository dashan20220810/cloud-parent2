package com.baisha.userserver.mq;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
import com.baisha.modulecommon.vo.mq.userServer.BetAmountVO;
import com.baisha.modulecommon.vo.mq.userServer.BetSettleUserVO;
import com.baisha.modulecommon.vo.mq.userServer.PlayMoneyAmountVO;
import com.baisha.userserver.business.RabbitBusiness;
import com.baisha.userserver.model.User;
import com.baisha.userserver.model.vo.balance.BalanceVO;
import com.baisha.userserver.model.vo.balance.PlayMoneyVO;
import com.baisha.userserver.service.UserService;
import com.baisha.userserver.util.constants.UserServerConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Component
public class DirectReceiver {
    //@Autowired
    //private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitBusiness rabbitBusiness;
    @Autowired
    private UserService userService;

    /**
     * 注单 引起 打码量/余额 变化
     * 派奖
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.USER_SETTLEMENT_ASSETS)
    public void betSettlementAward(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("结算(betSettlementAward)-收到gameServer参数不能为空");
            return;
        }
        BetSettleUserVO vo = JSONObject.parseObject(jsonStr, BetSettleUserVO.class);
        log.info("结算(betSettlementAward)-收到gameServer参数 {}", JSONObject.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getNoActive()) || null == vo.getBetId() || null == vo.getUserId()) {
            log.error("参数不全jsonStr={}", jsonStr);
            return;
        }
        //获取用户
        User user = userService.findById(vo.getUserId());
        if (Objects.isNull(user)) {
            log.error("会员不存在");
            return;
        }
        log.info("=====betSettlementAward============START==========================");
        //同一个人，同步
        synchronized (user.getId() + UserServerConstants.BALANCE + UserServerConstants.PLAYMONEY) {
            if (vo.getPlayMoney().compareTo(BigDecimal.ZERO) > 0) {
                PlayMoneyVO playMoneyVO = new PlayMoneyVO();
                playMoneyVO.setUserId(vo.getUserId());
                playMoneyVO.setAmount(vo.getPlayMoney());
                playMoneyVO.setPlayMoneyType(UserServerConstants.EXPENSES);
                playMoneyVO.setRemark("会员结算打码量，注单ID为" + vo.getBetId());
                playMoneyVO.setRelateId(vo.getBetId());
                if (vo.getIsReopen().equals(Constants.open)) {
                    playMoneyVO.setChangeType(PlayMoneyChangeEnum.SETTLEMENT_REOPEN.getCode());
                } else {
                    playMoneyVO.setChangeType(PlayMoneyChangeEnum.SETTLEMENT.getCode());
                }
                rabbitBusiness.doUserPlayMoney(user, playMoneyVO);
            }

            if (vo.getFinalAmount().compareTo(BigDecimal.ZERO) > 0) {
                BalanceVO balanceVO = new BalanceVO();
                balanceVO.setUserId(vo.getUserId());
                balanceVO.setBalanceType(UserServerConstants.INCOME);
                balanceVO.setAmount(vo.getFinalAmount());
                balanceVO.setRelateId(vo.getBetId());
                if (vo.getIsReopen().equals(Constants.open)) {
                    balanceVO.setChangeType(BalanceChangeEnum.BET_REWIN.getCode());
                } else {
                    balanceVO.setChangeType(BalanceChangeEnum.WIN.getCode());
                }
                balanceVO.setRemark("会员" + "在局号为" + vo.getNoActive() + "中奖");
                rabbitBusiness.doUserBalance(user, balanceVO);
            }
        }
        log.info("====betSettlementAward============END========================");
    }


    /**
     * 重新开牌 减去 会员 资产
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.USER_SUBTRACT_ASSETS)
    public void userSubtractAssets(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("重新开牌(userSubtractAssets)-收到gameServer参数不能为空");
            return;
        }
        BetAmountVO vo = JSONObject.parseObject(jsonStr, BetAmountVO.class);
        log.info("重新开牌(userSubtractAssets)-收到gameServer参数 {}", JSONObject.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getNoActive()) || null == vo.getBetId() || null == vo.getUserId()) {
            log.error("重新开牌-参数不全jsonStr={}", jsonStr);
            return;
        }
        //获取用户
        User user = userService.findById(vo.getUserId());
        if (Objects.isNull(user)) {
            log.error("会员不存在");
            return;
        }
        log.info("=====userSubtractAssets============START==========================");
        BalanceVO balanceVO = new BalanceVO();
        balanceVO.setUserId(vo.getUserId());
        balanceVO.setBalanceType(UserServerConstants.EXPENSES);
        balanceVO.setAmount(vo.getAmount());
        balanceVO.setRelateId(vo.getBetId());
        balanceVO.setChangeType(vo.getChangeType());
        balanceVO.setRemark(vo.getRemark());
        //同一个人，同步
        synchronized (user.getId() + UserServerConstants.BALANCE) {
            rabbitBusiness.doUserSubtractBalance(user, balanceVO);
        }
        log.info("====userSubtractAssets============END========================");
    }

    /**
     * 重新开牌 加 会员 打码量
     *
     * @param jsonStr
     */
    @RabbitListener(queues = MqConstants.USER_ADD_PLAYMONEY_ASSETS)
    public void userAddPlayMoneyAssets(String jsonStr) {
        if (StringUtils.isEmpty(jsonStr)) {
            log.error("重新开牌(userAddPlayMoneyAssets)-收到gameServer参数不能为空");
            return;
        }

        PlayMoneyAmountVO vo = JSONObject.parseObject(jsonStr, PlayMoneyAmountVO.class);
        log.info("重新开牌(userAddPlayMoneyAssets)-收到gameServer参数 {}", JSONObject.toJSONString(vo));
        if (StringUtils.isEmpty(vo.getNoActive()) || null == vo.getBetId() || null == vo.getUserId()) {
            log.error("重新开牌-参数不全jsonStr={}", jsonStr);
            return;
        }
        //获取用户
        User user = userService.findById(vo.getUserId());
        if (Objects.isNull(user)) {
            log.error("会员不存在");
            return;
        }
        log.info("=====userAddPlayMoneyAssets============START==========================");
        PlayMoneyVO playMoneyVO = new PlayMoneyVO();
        playMoneyVO.setUserId(vo.getUserId());
        playMoneyVO.setPlayMoneyType(UserServerConstants.INCOME);
        playMoneyVO.setAmount(vo.getPlayMoney());
        playMoneyVO.setRelateId(vo.getBetId());
        playMoneyVO.setChangeType(vo.getChangeType());
        playMoneyVO.setRemark(vo.getRemark());
        //同一个人，同步
        synchronized (user.getId() + UserServerConstants.PLAYMONEY) {
            rabbitBusiness.doUserAddPlayMoney(user, playMoneyVO);
        }
        log.info("====userAddPlayMoneyAssets============END========================");
    }


}
