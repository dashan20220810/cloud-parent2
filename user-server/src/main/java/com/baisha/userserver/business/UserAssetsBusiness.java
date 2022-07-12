package com.baisha.userserver.business;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.PlayMoneyChange;
import com.baisha.userserver.model.User;
import com.baisha.userserver.model.bo.BalanceBO;
import com.baisha.userserver.model.vo.balance.BalanceVO;
import com.baisha.userserver.model.vo.balance.PlayMoneyVO;
import com.baisha.userserver.service.AssetsService;
import com.baisha.userserver.service.BalanceChangeService;
import com.baisha.userserver.service.PlayMoneyChangeService;
import com.baisha.userserver.util.constants.RedisConstants;
import com.baisha.userserver.util.constants.UserServerConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserAssetsBusiness {

    @Autowired
    private RedissonClient redisson;
    @Autowired
    private AssetsService assetsService;
    @Autowired
    private BalanceChangeService balanceChangeService;
    @Autowired
    private PlayMoneyChangeService playMoneyChangeService;

    /**
     * 余额
     *
     * @param user
     * @param vo
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity doBalanceBusiness(User user, BalanceVO vo) {
        //使用用户ID 使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.USER_ASSETS + user.getId());
        try {
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
        } catch (Exception e) {
            fairLock.unlock();
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return ResponseUtil.fail();

    }

    private ResponseEntity doReduceBalance(User user, BalanceVO vo) {
        Assets assets = findAssetsByUserId(user.getId());
        if (assets.getBalance().compareTo(vo.getAmount()) < 0) {
            return new ResponseEntity("余额不足");
        }
        //支出先扣钱
        int flag = assetsService.doReduceBalanceById(vo.getAmount(), assets.getId());
        if (flag < 1) {
            log.info("(支出)更新余额失败(userId={} assetsId={})", user.getId(), assets.getId());
            return ResponseUtil.fail();
        }
        log.info("(支出)更新余额成功(userId={} assetsId={})", user.getId(), assets.getId());
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setUserId(user.getId());
        balanceChange.setBalanceType(UserServerConstants.EXPENSES);
        balanceChange.setRemark(vo.getRemark());
        balanceChange.setBeforeAmount(assets.getBalance());
        balanceChange.setAmount(vo.getAmount());
        balanceChange.setAfterAmount(assets.getBalance().subtract(vo.getAmount()));
        balanceChange.setChangeType(vo.getChangeType());
        balanceChange.setRelateId(vo.getRelateId());
        BalanceChange bc = balanceChangeService.save(balanceChange);
        if (Objects.nonNull(bc)) {
            log.info("(支出)创建余额变化成功(userId={})", user.getId());
            return ResponseUtil.success();
        }
        log.info("(支出)创建余额变化失败(userId={})", user.getId());
        return ResponseUtil.fail();
    }

    private ResponseEntity doIncomeBalance(User user, BalanceVO vo) {
        Assets assets = findAssetsByUserId(user.getId());
        //插入余额变动表
        BalanceChange balanceChange = new BalanceChange();
        balanceChange.setUserId(user.getId());
        balanceChange.setBalanceType(UserServerConstants.INCOME);
        balanceChange.setRemark(vo.getRemark());
        balanceChange.setBeforeAmount(assets.getBalance());
        balanceChange.setAmount(vo.getAmount());
        balanceChange.setAfterAmount(assets.getBalance().add(vo.getAmount()));
        balanceChange.setChangeType(vo.getChangeType());
        balanceChange.setRelateId(vo.getRelateId());
        BalanceChange bc = balanceChangeService.save(balanceChange);
        if (Objects.nonNull(bc)) {
            log.info("(收入)创建余额变化成功(userId={})", user.getId());
            //更新余额
            int flag = assetsService.doIncreaseBalanceById(vo.getAmount(), assets.getId());
            if (flag < 1) {
                log.info("(收入)更新余额失败(userId={} assetsId={})", user.getId(), assets.getId());
                return ResponseUtil.fail();
            }
            log.info("(收入)更新余额成功(userId={} assetsId={})", user.getId(), assets.getId());
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


    public BalanceBO getUserBalance(Long userId) {
        DecimalFormat df = new DecimalFormat("#0.00");
        Assets assets = findAssetsByUserId(userId);
        BigDecimal balance = assets.getBalance().setScale(2, RoundingMode.HALF_UP);
        return BalanceBO.builder().balance(df.format(balance)).build();
    }


    /**
     * 打码量
     *
     * @param user
     * @param vo
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity doPlayMoneyBusiness(User user, PlayMoneyVO vo) {
        //使用用户ID 使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.USER_ASSETS + user.getId());
        try {
            boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                if (UserServerConstants.INCOME == vo.getPlayMoneyType()) {
                    //收入
                    ResponseEntity response = doIncomePlayMoney(user, vo);
                    fairLock.unlock();
                    return response;
                }
                if (UserServerConstants.EXPENSES == vo.getPlayMoneyType()) {
                    //支出
                    ResponseEntity response = doReducePlayMoney(user, vo);
                    fairLock.unlock();
                    return response;
                }
            }
        } catch (Exception e) {
            fairLock.unlock();
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return ResponseUtil.fail();
    }

    /**
     * 打码量减少
     *
     * @param user
     * @param vo
     * @return
     */
    private ResponseEntity doReducePlayMoney(User user, PlayMoneyVO vo) {
        Assets assets = findAssetsByUserId(user.getId());
        if (assets.getPlayMoney().compareTo(BigDecimal.ONE) < 0) {
            log.info("打码量小于1,不需要打码(userId={} assetsId={})", user.getId(), assets.getId());
            return ResponseUtil.success();
        }
        if (assets.getPlayMoney().compareTo(vo.getAmount()) <= 0) {
            log.info("最后一笔打码(userId={} assetsId={})", user.getId(), assets.getId());
            vo.setAmount(assets.getPlayMoney());
        }

        //支出先扣钱
        int flag = assetsService.doReducePlayMoneyById(vo.getAmount(), assets.getId());
        if (flag < 1) {
            log.info("(支出)更新打码量失败(userId={} assetsId={})", user.getId(), assets.getId());
            return ResponseUtil.fail();
        }
        log.info("(支出)更新打码量成功(userId={} assetsId={})", user.getId(), assets.getId());
        PlayMoneyChange change = new PlayMoneyChange();
        change.setUserId(user.getId());
        change.setPlayMoneyType(UserServerConstants.EXPENSES);
        change.setRemark(vo.getRemark());
        change.setBeforeAmount(assets.getPlayMoney());
        change.setAmount(vo.getAmount());
        change.setAfterAmount(assets.getPlayMoney().subtract(vo.getAmount()));
        change.setChangeType(vo.getChangeType());
        change.setRelateId(vo.getRelateId());
        PlayMoneyChange pc = playMoneyChangeService.save(change);
        if (Objects.nonNull(pc)) {
            log.info("(支出)创建打码量变化成功(userId={})", user.getId());
            return ResponseUtil.success();
        }
        log.info("(支出)创建打码量变化失败(userId={})", user.getId());
        return ResponseUtil.fail();
    }

    /**
     * 打码量增加
     *
     * @param user
     * @param vo
     * @return
     */
    private ResponseEntity doIncomePlayMoney(User user, PlayMoneyVO vo) {
        Assets assets = findAssetsByUserId(user.getId());
        //插入打码量变动表
        PlayMoneyChange change = new PlayMoneyChange();
        change.setUserId(user.getId());
        change.setPlayMoneyType(UserServerConstants.INCOME);
        change.setRemark(vo.getRemark());
        change.setBeforeAmount(assets.getPlayMoney());
        change.setAmount(vo.getAmount());
        change.setAfterAmount(assets.getPlayMoney().add(vo.getAmount()));
        change.setChangeType(vo.getChangeType());
        change.setRelateId(vo.getRelateId());
        PlayMoneyChange pc = playMoneyChangeService.save(change);
        if (Objects.nonNull(pc)) {
            log.info("(收入)创建打码量变化成功(userId={})", user.getId());
            //更新打码量
            int flag = assetsService.doIncreasePlayMoneyById(vo.getAmount(), assets.getId());
            if (flag < 1) {
                log.info("(收入)更新打码量失败(userId={} assetsId={})", user.getId(), assets.getId());
                return ResponseUtil.fail();
            }
            log.info("(收入)更新打码量成功(userId={} assetsId={})", user.getId(), assets.getId());
            return ResponseUtil.success();
        }
        log.info("(收入)创建打码量变化失败(userId={})", user.getId());
        return ResponseUtil.fail();
    }


}
