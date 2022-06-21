package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.bo.BalanceBO;
import com.baisha.userserver.business.BalanceService;
import com.baisha.userserver.model.Assets;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.User;
import com.baisha.userserver.service.AssetsService;
import com.baisha.userserver.service.UserService;
import com.baisha.userserver.vo.balance.BalanceVO;
import com.baisha.userserver.vo.user.UserSearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

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
    private BalanceService balanceService;


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

        ResponseEntity res = balanceService.doBalanceBusiness(user, vo);
        return res;
    }


}
