package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.business.UserAssetsBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.assets.AdjustmentTypeBO;
import com.baisha.backendserver.model.bo.assets.OrderAdjustmentTypeBO;
import com.baisha.backendserver.model.bo.order.SsOrderAddBO;
import com.baisha.backendserver.model.bo.user.UserAssetsBO;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.assets.TgUserVO;
import com.baisha.backendserver.model.vo.assets.UserApplyReduceVO;
import com.baisha.backendserver.model.vo.assets.UserApplyVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
import com.baisha.modulecommon.enums.order.OrderAdjustmentTypeEnum;
import com.baisha.modulecommon.enums.order.OrderAdjustmentTypeTxEnum;
import com.baisha.modulecommon.enums.order.OrderStatusEnum;
import com.baisha.modulecommon.enums.order.OrderTypeEnum;
import com.baisha.modulecommon.reponse.ResponseCode;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("assets")
@Api(tags = "资金管理")
public class UserAssetsController {

    @Value("${url.userServer}")
    private String userServerUrl;
    @Autowired
    private CommonBusiness commonService;
    @Autowired
    private UserAssetsBusiness userAssetsBusiness;


    @ApiOperation(value = "用户个人资产")
    @GetMapping("findAssetsTgUserId")
    public ResponseEntity<UserAssetsBO> findAssetsTgUserId(TgUserVO vo) {
        if (StringUtils.isEmpty(vo.getTgUserId())) {
            return ResponseUtil.parameterNotNull();
        }
        return findAssetsTgUserId(vo.getTgUserId());
    }

    private ResponseEntity<UserAssetsBO> findAssetsTgUserId(String tgUserId) {
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_BYTGUSERID + "?tgUserId=" + tgUserId;
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }

    @ApiOperation(value = "调整类型")
    @GetMapping("getAdjustmentType")
    public ResponseEntity<AdjustmentTypeBO> getAdjustmentType() {
        AdjustmentTypeBO bo = new AdjustmentTypeBO();
        //充值
        List<OrderAdjustmentTypeEnum> charge = OrderAdjustmentTypeEnum.getList();
        bo.setCharge(charge.stream().map(item -> OrderAdjustmentTypeBO.builder()
                .code(item.getCode()).name(item.getName()).build()).toList());
        //提现
        List<OrderAdjustmentTypeTxEnum> withdraw = OrderAdjustmentTypeTxEnum.getList();
        bo.setWithdraw(withdraw.stream().map(item -> OrderAdjustmentTypeBO.builder()
                .code(item.getCode()).name(item.getName()).build()).toList());
        return ResponseUtil.success(bo);
    }


    @ApiOperation(value = "会员人工添加额度申请")
    @PostMapping("userApplyAddBalance")
    public ResponseEntity<SsOrderAddBO> doUserApplyAddBalance(UserApplyVO vo) {
        if (null == vo.getUserId() || null == vo.getTgUserId() || null == vo.getAmount()
                || null == vo.getAdjustmentType() || null == vo.getFlowMultiple()) {
            return ResponseUtil.parameterNotNull();
        }
        Integer ZERO = 0;
        if (vo.getFlowMultiple().compareTo(ZERO) < 0 || vo.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseEntity("数字不规范");
        }
        //提交申请
        Map param = BackendServerUtil.objectToMap(vo);
        param.put("orderType", OrderTypeEnum.CHARGE_ORDER.getCode());
        param.put("orderStatus", OrderStatusEnum.ORDER_WAIT.getCode());
        log.info("会员人工添加额度申请 参数：{}", JSONObject.toJSONString(param));
        //去userServer添加order
        String url = userServerUrl + UserServerConstants.USER_ORDER_ADD;
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        ResponseEntity orderResponse = JSON.parseObject(result, ResponseEntity.class);
        if (orderResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    currentUser.getUserName() + "为用户id={" + vo.getUserId() + "}新增充值订单成功", BackendConstants.ORDER);

            //提交申请记录 做审核用
            //SsOrderAddBO SsOrderAddBO = JSONObject.parseObject(orderResponse.getData().toString(), SsOrderAddBO.class);
        }
        return orderResponse;
    }

    @ApiOperation(value = "会员人工扣除额度")
    @PostMapping("userApplyReduceBalance")
    public ResponseEntity<SsOrderAddBO> doUserApplyReduceBalance(UserApplyReduceVO vo) {
        if (null == vo.getUserId() || null == vo.getTgUserId() || null == vo.getAmount() || null == vo.getAdjustmentType()) {
            return ResponseUtil.parameterNotNull();
        }
        if (vo.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseEntity("数字不规范");
        }

        //检查是否有打码量
        ResponseEntity assetsResponse = findAssetsTgUserId(vo.getTgUserId());
        if (Objects.isNull(assetsResponse) || assetsResponse.getCode() != ResponseCode.SUCCESS.getCode()) {
            return new ResponseEntity<>("无资产");
        }
        UserAssetsBO userAssetsBO = JSONObject.parseObject(assetsResponse.getData().toString(), UserAssetsBO.class);
        if (userAssetsBO.getPlayMoney().compareTo(BigDecimal.ONE) >= 0) {
            return new ResponseEntity("不能下分，流水不足");
        }
        if (vo.getAmount().compareTo(userAssetsBO.getBalance()) > 0) {
            //操作金额大于查询余额时可以正常操作人工扣除额度，系统会扣除到余额为零
            vo.setAmount(userAssetsBO.getBalance());
        }

        //提交申请
        Map param = BackendServerUtil.objectToMap(vo);
        param.put("orderType", OrderTypeEnum.WITHDRAW_ORDER.getCode());
        param.put("orderStatus", OrderStatusEnum.ORDER_WAIT.getCode());
        log.info("会员人工扣除额度 参数：{}", JSONObject.toJSONString(param));
        //去userServer添加order
        String url = userServerUrl + UserServerConstants.USER_ORDER_ADD;
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        ResponseEntity orderResponse = JSON.parseObject(result, ResponseEntity.class);
        if (orderResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    currentUser.getUserName() + "为用户id={" + vo.getUserId() + "}新增提现(下分)订单成功", BackendConstants.ORDER);

            //提交申请记录 做审核用
            //SsOrderAddBO SsOrderAddBO = JSONObject.parseObject(orderResponse.getData().toString(), SsOrderAddBO.class);

        }
        return orderResponse;
    }


}
