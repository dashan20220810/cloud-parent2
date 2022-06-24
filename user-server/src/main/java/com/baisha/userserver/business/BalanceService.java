package com.baisha.userserver.business;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.util.constants.RedisConstants;
import com.baisha.userserver.util.constants.UserServerConstants;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.User;
import com.baisha.userserver.service.AssetsService;
import com.baisha.userserver.service.BalanceChangeService;
import com.baisha.userserver.model.vo.balance.BalanceVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BalanceService {

    @Autowired
    private RedissonClient redisson;
    @Autowired
    private AssetsService assetsService;
    @Autowired
    private BalanceChangeService balanceChangeService;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity doBalanceBusiness(User user, BalanceVO vo) throws Exception {
        RLock fairLock = redisson.getFairLock(RedisConstants.USER_BALANCE + user.getId());
        boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
        if (res) {
            if (UserServerConstants.INCOME == vo.getBalanceType()) {
                //收入
                ResponseEntity response = doIncomeBalance(user, vo);
                fairLock.unlock();
                return response;
            }
            if (UserServerConstants.EXPENSES == vo.getBalanceType()) {
                //支出
                ResponseEntity response = doReduceBalance(user, vo);
                fairLock.unlock();
                return response;
            }
        }
        return ResponseUtil.fail();

    }

    private ResponseEntity doReduceBalance(User user, BalanceVO vo) {
        Assets assets = findAssetsByUserId(user.getId());
        if (assets.getBalance().compareTo(vo.getAmount()) < 0) {
            return new ResponseEntity("余额不足");
        }
        //支出先扣钱
        int flag = assetsService.doReduceBalanceByUserId(vo.getAmount(), user.getId());
        if (flag < 1) {
            log.info("(支出)更新余额失败(userId={})", user.getId());
            return ResponseUtil.fail();
        }
        log.info("(支出)更新余额成功(userId={})", user.getId());
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setUserId(user.getId());
        balanceChange.setBalanceType(UserServerConstants.EXPENSES);
        balanceChange.setRemark(vo.getRemark());
        balanceChange.setBeforeAmount(assets.getBalance());
        balanceChange.setAmount(vo.getAmount());
        balanceChange.setAfterAmount(assets.getBalance().subtract(vo.getAmount()));
        BalanceChange bc = balanceChangeService.save(balanceChange);
        if (Objects.nonNull(bc)) {
            log.info("(支出)创建余额变化成功(userId={})", user.getId());
            return ResponseUtil.success();
        }
        log.info("(支出)创建余额变化失败(userId={})", user.getId());
        return ResponseUtil.fail();
    }

    private ResponseEntity doIncomeBalance(User user, BalanceVO vo) {
        //使用用户ID 使用redisson 公平锁
        Assets assets = findAssetsByUserId(user.getId());
        //插入余额变动表
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setUserId(user.getId());
        balanceChange.setBalanceType(UserServerConstants.INCOME);
        balanceChange.setRemark(vo.getRemark());
        balanceChange.setBeforeAmount(assets.getBalance());
        balanceChange.setAmount(vo.getAmount());
        balanceChange.setAfterAmount(assets.getBalance().add(vo.getAmount()));
        BalanceChange bc = balanceChangeService.save(balanceChange);
        if (Objects.nonNull(bc)) {
            log.info("(收入)创建余额变化成功(userId={})", user.getId());
            //更新余额
            int flag = assetsService.doIncreaseBalanceByUserId(vo.getAmount(), user.getId());
            if (flag < 1) {
                log.info("(收入)更新余额失败(userId={})", user.getId());
                return ResponseUtil.fail();
            }
            log.info("(收入)更新余额成功(userId={})", user.getId());
            return ResponseUtil.success();
        }
        log.info("(收入)创建余额变化失败(userId={})", user.getId());
        return ResponseUtil.fail();
    }

    public Assets findAssetsByUserId(Long userId) {
        Assets assets = assetsService.getAssetsByUserId(userId);
        if (Objects.isNull(assets)) {
            assets = new Assets();
            assets.setUserId(userId);
            assetsService.saveAssets(assets);
            return assets;
        }
        return assets;
    }


}
