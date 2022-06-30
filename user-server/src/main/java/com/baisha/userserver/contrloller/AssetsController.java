package com.baisha.userserver.contrloller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.userserver.business.UserAssetsService;
import com.baisha.userserver.model.BalanceChange;
import com.baisha.userserver.model.User;
import com.baisha.userserver.model.bo.BalanceBO;
import com.baisha.userserver.model.vo.UserIdVO;
import com.baisha.userserver.model.vo.balance.BalanceVO;
import com.baisha.userserver.model.vo.balance.PlayMoneyVO;
import com.baisha.userserver.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private UserAssetsService userAssetsService;


    @ApiOperation(("查询用户余额"))
    @GetMapping("query")
    public ResponseEntity<BalanceBO> query(UserIdVO vo) {
        if (Objects.isNull(vo.getUserId()) || vo.getUserId() < 0) {
            return new ResponseEntity("用户名ID不规范");
        }
        //获取用户
        User user = userService.findById(vo.getUserId());
        if (Objects.isNull(user)) {
            //会员不存在
            return ResponseUtil.success(BalanceBO.builder().balance("0.00").build());
        }
        BalanceBO balanceBO = userAssetsService.getUserBalance(vo.getUserId());
        return ResponseUtil.success(balanceBO);
    }

    @ApiOperation(("用户增加/减少余额"))
    @PostMapping("balance")
    public ResponseEntity balance(BalanceVO vo) throws Exception {
        if (Objects.isNull(vo.getUserId()) || vo.getUserId() < 0) {
            return new ResponseEntity("用户名ID不规范");
        }
        if (BalanceChange.checkBalanceType(vo.getBalanceType())) {
            return new ResponseEntity("收支类型不规范");
        }
        if (BalanceChange.checkAmount(vo.getAmount())) {
            return new ResponseEntity("金额必须大于0");
        }
        //获取用户
        User user = userService.findById(vo.getUserId());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }

        ResponseEntity res = userAssetsService.doBalanceBusiness(user, vo);
        return res;
    }

    @ApiOperation(("用户增加/减少打码量"))
    @PostMapping("playMoney")
    public ResponseEntity doPlayMoney(PlayMoneyVO vo) throws Exception {
        if (Objects.isNull(vo.getUserId()) || vo.getUserId() < 0) {
            return new ResponseEntity("用户名ID不规范");
        }
        if (BalanceChange.checkBalanceType(vo.getPlayMoneyType())) {
            return new ResponseEntity("收支类型不规范");
        }
        if (BalanceChange.checkAmount(vo.getAmount())) {
            return new ResponseEntity("金额必须大于0");
        }
        //获取用户
        User user = userService.findById(vo.getUserId());
        if (Objects.isNull(user)) {
            return new ResponseEntity("会员不存在");
        }

        ResponseEntity res = userAssetsService.doPlayMoneyBusiness(user, vo);
        return res;
    }


}
