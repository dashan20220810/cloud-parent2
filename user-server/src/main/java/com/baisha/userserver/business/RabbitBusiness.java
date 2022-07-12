package com.baisha.userserver.business;

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
        userAssetsBusiness.doBalanceBusiness(user, balanceVO);
    }

    @Async(value = "asyncExecutor")
    public void doUserPlayMoney(User user, PlayMoneyVO playMoneyVO) {
        userAssetsBusiness.doPlayMoneyBusiness(user, playMoneyVO);
    }

}
