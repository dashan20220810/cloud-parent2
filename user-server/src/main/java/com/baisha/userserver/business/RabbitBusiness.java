package com.baisha.userserver.business;

import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
import com.baisha.userserver.model.User;
import com.baisha.userserver.model.vo.balance.BalanceVO;
import com.baisha.userserver.model.vo.balance.PlayMoneyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitBusiness {

    @Autowired
    private UserAssetsBusiness userAssetsBusiness;

    @Async(value = "asyncExecutor")
    public void doUserBalance(User user, BalanceVO balanceVO) {
        if (BalanceChangeEnum.BET_REWIN.getCode().equals(balanceVO.getChangeType())) {
            //支持多次重新开奖
            log.info("doUserBalance-重新派彩");
            userAssetsBusiness.doAddBalanceBusiness(user, balanceVO);
        } else {
            userAssetsBusiness.doBalanceBusiness(user, balanceVO);
        }
    }

    @Async(value = "asyncExecutor")
    public void doUserPlayMoney(User user, PlayMoneyVO playMoneyVO) {
        if (PlayMoneyChangeEnum.SETTLEMENT_REOPEN.getCode().equals(playMoneyVO.getChangeType())) {
            //支持多次重新开奖
            log.info("doUserPlayMoney-重新派彩");
            userAssetsBusiness.doSubtractPlayMoneyBusiness(user, playMoneyVO);
        } else {
            userAssetsBusiness.doPlayMoneyBusiness(user, playMoneyVO);
        }
    }

    @Async(value = "asyncExecutor")
    public void doUserSubtractBalance(User user, BalanceVO balanceVO) {
        userAssetsBusiness.doSubtractBalanceBusiness(user, balanceVO);
    }

    @Async(value = "asyncExecutor")
    public void doUserAddPlayMoney(User user, PlayMoneyVO playMoneyVO) {
        userAssetsBusiness.doUserAddPlayMoneyBusiness(user, playMoneyVO);
    }


}
