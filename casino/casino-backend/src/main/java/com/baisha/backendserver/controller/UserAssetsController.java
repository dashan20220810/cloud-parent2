package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.assets.AdjustmentTypeBO;
import com.baisha.backendserver.model.bo.assets.OrderAdjustmentTypeBO;
import com.baisha.backendserver.model.bo.order.SsOrderAddBO;
import com.baisha.backendserver.model.bo.user.UserAssetsBO;
import com.baisha.backendserver.model.vo.assets.TgUserVO;
import com.baisha.backendserver.model.vo.assets.UserApplyReduceVO;
import com.baisha.backendserver.model.vo.assets.UserApplyVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
import com.baisha.modulecommon.enums.order.OrderAdjustmentTypeEnum;
import com.baisha.modulecommon.enums.order.OrderAdjustmentTypeTxEnum;
import com.baisha.modulecommon.enums.order.OrderStatusEnum;
import com.baisha.modulecommon.enums.order.OrderTypeEnum;
import com.baisha.modulecommon.enums.user.UserTypeEnum;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("assets")
@Api(tags = "????????????")
public class UserAssetsController {

    @Value("${url.userServer}")
    private String userServerUrl;
    @Autowired
    private CommonBusiness commonService;


    @ApiOperation(value = "????????????????????????")
    @GetMapping("findAssetsTgUserId")
    public ResponseEntity<UserAssetsBO> findAssetsTgUserId(TgUserVO vo) {
        if (StringUtils.isEmpty(vo.getTgUserId())) {
            return ResponseUtil.parameterNotNull();
        }
        UserAssetsBO bo = findAssetsTgUserId(vo.getTgUserId());
        if (Objects.nonNull(bo)) {
            return ResponseUtil.success(bo);
        } else {
            return new ResponseEntity("???????????????");
        }
    }

    private UserAssetsBO findAssetsTgUserId(String tgUserId) {
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_BYTGUSERID + "?tgUserId=" + tgUserId;
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return null;
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (Objects.nonNull(responseEntity) && responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            UserAssetsBO userAssetsBO = JSONObject.parseObject(JSONObject.toJSONString(responseEntity.getData()), UserAssetsBO.class);
            if (null == userAssetsBO.getUserType()) {
                userAssetsBO.setUserTypeName(UserTypeEnum.NORMAL.getName());
            } else {
                userAssetsBO.setUserTypeName(UserTypeEnum.nameOfCode(userAssetsBO.getUserType()).getName());
            }
            return userAssetsBO;
        }
        return null;
    }

    @ApiOperation(value = "????????????(????????????)")
    @GetMapping("addAdjustmentType")
    public ResponseEntity<List<OrderAdjustmentTypeBO>> getAddAdjustmentType() {
        //??????
        List<OrderAdjustmentTypeEnum> charge = OrderAdjustmentTypeEnum.getList();
        List<OrderAdjustmentTypeBO> list = charge.stream().map(item -> OrderAdjustmentTypeBO.builder()
                .code(item.getCode()).name(item.getName()).build()).toList();
        return ResponseUtil.success(list);
    }

    @ApiOperation(value = "????????????(????????????)")
    @GetMapping("subAdjustmentType")
    public ResponseEntity<List<OrderAdjustmentTypeBO>> getSubAdjustmentType() {
        //??????
        List<OrderAdjustmentTypeTxEnum> withdraw = OrderAdjustmentTypeTxEnum.getList();
        List<OrderAdjustmentTypeBO> list = withdraw.stream().map(item -> OrderAdjustmentTypeBO.builder()
                .code(item.getCode()).name(item.getName()).build()).toList();
        return ResponseUtil.success(list);
    }


    //@ApiOperation(value = "??????????????????????????????")
    //@PostMapping("userApplyAddBalance")
    public ResponseEntity<SsOrderAddBO> doUserApplyAddBalance(UserApplyVO vo) {
        if (null == vo.getUserId() || null == vo.getTgUserId() || null == vo.getAmount()
                || null == vo.getAdjustmentType() || null == vo.getFlowMultiple()) {
            return ResponseUtil.parameterNotNull();
        }
        if (vo.getFlowMultiple().compareTo(BigDecimal.ZERO) < 0 || vo.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseEntity("???????????????");
        }
        //????????????
        Map param = BackendServerUtil.objectToMap(vo);
        param.put("orderType", OrderTypeEnum.CHARGE_ORDER.getCode());
        param.put("orderStatus", OrderStatusEnum.ORDER_WAIT.getCode());
        log.info("?????????????????????????????? ?????????{}", JSONObject.toJSONString(param));
        //???userServer??????order
        String url = userServerUrl + UserServerConstants.USER_ORDER_ADD;
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        ResponseEntity orderResponse = JSON.parseObject(result, ResponseEntity.class);
        if (orderResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    currentUser.getUserName() + "?????????id={" + vo.getUserId() + "}????????????????????????", BackendConstants.ORDER_MODULE);

            //?????????????????? ????????????
            //SsOrderAddBO SsOrderAddBO = JSONObject.parseObject(orderResponse.getData().toString(), SsOrderAddBO.class);
        }
        return orderResponse;
    }

    //@ApiOperation(value = "????????????????????????")
    //@PostMapping("userApplyReduceBalance")
    public ResponseEntity<SsOrderAddBO> doUserApplyReduceBalance(UserApplyReduceVO vo) {
        if (null == vo.getUserId() || null == vo.getTgUserId() || null == vo.getAmount() || null == vo.getAdjustmentType()) {
            return ResponseUtil.parameterNotNull();
        }
        if (vo.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseEntity("???????????????");
        }

        //????????????????????????
        UserAssetsBO userAssetsBO = findAssetsTgUserId(vo.getTgUserId());
        if (Objects.isNull(userAssetsBO)) {
            return new ResponseEntity<>("?????????");
        }
        if (userAssetsBO.getPlayMoney().compareTo(BigDecimal.ONE) >= 0) {
            return new ResponseEntity("???????????????????????????");
        }
        if (vo.getAmount().compareTo(userAssetsBO.getBalance()) > 0) {
            //??????????????????????????????????????????????????????????????????????????????????????????????????????
            vo.setAmount(userAssetsBO.getBalance());
        }

        //????????????
        Map param = BackendServerUtil.objectToMap(vo);
        param.put("orderType", OrderTypeEnum.WITHDRAW_ORDER.getCode());
        param.put("orderStatus", OrderStatusEnum.ORDER_WAIT.getCode());
        log.info("???????????????????????? ?????????{}", JSONObject.toJSONString(param));
        //???userServer??????order
        String url = userServerUrl + UserServerConstants.USER_ORDER_ADD;
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        ResponseEntity orderResponse = JSON.parseObject(result, ResponseEntity.class);
        if (orderResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    currentUser.getUserName() + "?????????id={" + vo.getUserId() + "}????????????(??????)????????????", BackendConstants.ORDER_MODULE);

            //?????????????????? ????????????
            //SsOrderAddBO SsOrderAddBO = JSONObject.parseObject(orderResponse.getData().toString(), SsOrderAddBO.class);

        }
        return orderResponse;
    }


}
