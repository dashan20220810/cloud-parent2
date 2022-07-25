package com.baisha.userserver.business;

import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
import com.baisha.userserver.model.User;
import com.baisha.userserver.model.vo.balance.BalanceVO;
import com.baisha.userserver.model.vo.balance.PlayMoneyVO;
import com.baisha.userserver.util.constants.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RabbitBusiness {

    @Autowired
    private UserAssetsBusiness userAssetsBusiness;

    @Autowired
    private RedissonClient redisson;

    @Async(value = "asyncExecutor")
    public void doUserBalance(User user, BalanceVO balanceVO) {
        //使用分布式锁 才能保证在分布式下 结算正确
        // synchronized (RedisConstants.BALANCE + user.getId()) {
        RLock fairLock = redisson.getFairLock(RedisConstants.BALANCE + user.getId());
        try {
            boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //抢到锁
                if (BalanceChangeEnum.BET_REWIN.getCode().equals(balanceVO.getChangeType())) {
                    //支持多次重新开奖
                    log.info("doUserBalance-重新派彩");
                    userAssetsBusiness.doAddBalanceBusiness(user, balanceVO);
                } else {
                    userAssetsBusiness.doBalanceBusiness(user, balanceVO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁 保证流程已完成
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }

    @Async(value = "asyncExecutor")
    public void doUserPlayMoney(User user, PlayMoneyVO playMoneyVO) {
        //synchronized (RedisConstants.PLAYMONEY + user.getId()) {
        RLock fairLock = redisson.getFairLock(RedisConstants.PLAYMONEY + user.getId());
        try {
            boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //抢到锁
                if (PlayMoneyChangeEnum.SETTLEMENT_REOPEN.getCode().equals(playMoneyVO.getChangeType())) {
                    //支持多次重新开奖
                    log.info("doUserPlayMoney-重新派彩");
                    userAssetsBusiness.doSubtractPlayMoneyBusiness(user, playMoneyVO);
                } else {
                    userAssetsBusiness.doPlayMoneyBusiness(user, playMoneyVO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁 保证流程已完成
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }


    }

    @Async(value = "asyncExecutor")
    public void doUserSubtractBalance(User user, BalanceVO balanceVO) {
        //synchronized (RedisConstants.BALANCE + user.getId()) {
        RLock fairLock = redisson.getFairLock(RedisConstants.BALANCE + user.getId());
        try {
            boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //抢到锁
                userAssetsBusiness.doSubtractBalanceBusiness(user, balanceVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁 保证流程已完成
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }

    @Async(value = "asyncExecutor")
    public void doUserAddPlayMoney(User user, PlayMoneyVO playMoneyVO) {
        //synchronized (RedisConstants.PLAYMONEY + user.getId()) {
        RLock fairLock = redisson.getFairLock(RedisConstants.PLAYMONEY + user.getId());
        try {
            boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                //抢到锁
                userAssetsBusiness.doUserAddPlayMoneyBusiness(user, playMoneyVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放锁 保证流程已完成
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }


}
