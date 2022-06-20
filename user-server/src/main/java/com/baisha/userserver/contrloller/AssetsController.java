package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.constants.RedisConstants;
import com.baisha.userserver.constants.UserServerConstants;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.User;
import com.baisha.userserver.response.BalanceBO;
import com.baisha.userserver.service.AssetsService;
import com.baisha.userserver.service.BalanceChangeService;
import com.baisha.userserver.service.UserService;
import com.baisha.userserver.vo.balance.BalanceVO;
import com.baisha.userserver.vo.user.UserSearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping("assets")
@Api(tags = "资产")
public class AssetsController {

    @Autowired
    private UserService userService;
    @Autowired
    private AssetsService assetsService;
    @Autowired
    private BalanceChangeService balanceChangeService;
    @Autowired
    private RedissonClient redisson;


    @ApiOperation(("查询用户余额"))
    @GetMapping("query")
    public ResponseEntity query(UserSearchVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        //获取用户
        User user = userService.findByUserName(vo.getUserName());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
        Assets assets = assetsService.getAssetsByUserId(user.getId());
        if (Objects.isNull(assets)) {
            return new ResponseEntity("资产不存在");
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        BigDecimal balance = assets.getBalance().setScale(2, RoundingMode.HALF_UP);
        return ResponseUtil.success(BalanceBO.builder().balance(df.format(balance)).build());
    }

    @ApiOperation(("用户上下分"))
    @PostMapping("balance")
    public ResponseEntity balance(BalanceVO vo) throws Exception {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (User.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (BalanceChange.checkBalanceType(vo.getBalanceType())) {
            return new ResponseEntity("收支类型不规范");
        }
        if (BalanceChange.checkAmount(vo.getAmount())) {
            return new ResponseEntity("金额必须大于0");
        }
        //获取用户
        User user = userService.findByUserName(vo.getUserName());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }
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
        Assets assets = assetsService.getAssetsByUserId(user.getId());
        if (Objects.isNull(assets)) {
            return new ResponseEntity("资产不存在");
        }
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
        //插入余额变动表
        Assets assets = assetsService.getAssetsByUserId(user.getId());
        if (Objects.isNull(assets)) {
            return new ResponseEntity("资产不存在");
        }
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


}
