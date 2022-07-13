package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.business.PlayMoneyBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.order.SsOrderAddBO;
import com.baisha.backendserver.model.bo.sys.SysPlayMoneyParameterBO;
import com.baisha.backendserver.model.bo.user.UserAssetsBO;
import com.baisha.backendserver.model.bo.user.UserBalanceChangePageBO;
import com.baisha.backendserver.model.bo.user.UserPageBO;
import com.baisha.backendserver.model.bo.user.UserPlayMoneyChangePageBO;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.order.SsOrderAddVO;
import com.baisha.backendserver.model.vo.user.*;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
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
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping("user")
@Api(tags = "用户管理")
public class UserController {

    @Value("${url.userServer}")
    private String userServerUrl;
    @Autowired
    private CommonBusiness commonService;
    @Autowired
    private PlayMoneyBusiness playMoneyService;

    @GetMapping("page")
    @ApiOperation(("用户分页"))
    public ResponseEntity<Page<UserPageBO>> page(UserPageVO vo) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append(userServerUrl + UserServerConstants.USERSERVER_USER_PAGE + "?pageNumber=" + vo.getPageNumber() +
                "&pageSize=" + vo.getPageSize());
        if (StringUtils.isNotBlank(vo.getUserName())) {
            sb.append("&userName=" + vo.getUserName());
        }
        if (StringUtils.isNotBlank(vo.getNickName())) {
            sb.append("&nickName=" + vo.getNickName());
        }
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sb.append("&startTime=" + URLEncoder.encode(vo.getStartTime().trim(), BackendConstants.UTF8));
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sb.append("&endTime=" + URLEncoder.encode(vo.getEndTime().trim(), BackendConstants.UTF8));
        }
        String url = sb.toString();
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }


    @ApiOperation(("删除用户"))
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (Objects.isNull(vo.getId())) {
            return ResponseUtil.parameterNotNull();
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USER_DELETE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.DELETE,
                    currentUser.getUserName() + "删除用户id={" + vo.getId() + "}", BackendConstants.USER_MODULE);
        }
        return responseEntity;
    }

    @ApiOperation(("启用/禁用用户"))
    @PostMapping("status")
    public ResponseEntity status(IdVO vo) {
        if (Objects.isNull(vo) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USER_STATUS;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    currentUser.getUserName() + "修改用户状态id={" + vo.getId() + "}", BackendConstants.USER_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation(value = "用户充值")
    @PostMapping("increaseBalance")
    public ResponseEntity increaseBalance(BalanceVO vo) {
        if (null == vo.getId() || vo.getId() < 0 || null == vo.getAmount() || vo.getAmount() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        if (BackendServerUtil.checkIntAmount(vo.getAmount())) {
            return new ResponseEntity("金额不规范");
        }

        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        //新增订单
        SsOrderAddVO order = chargeOrder(vo, currentUser);
        ResponseEntity orderResponseEntity = doCreateOrder(order);
        if (orderResponseEntity.getCode() != ResponseCode.SUCCESS.getCode()) {
            return ResponseUtil.fail();
        }
        SsOrderAddBO ssOrderAddBO = JSONObject.parseObject(JSONObject.toJSONString(orderResponseEntity.getData()), SsOrderAddBO.class);
        Long orderId = ssOrderAddBO.getId();
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                currentUser.getUserName() + "为用户id={" + vo.getId() + "}新增充值订单成功", BackendConstants.ORDER);
        //增加余额
        ResponseEntity balanceResponseEntity = doIncomeBalance(vo, orderId);
        if (balanceResponseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    currentUser.getUserName() + "为用户id={" + vo.getId() + "}增加余额成功", BackendConstants.USER_ASSETS_MODULE);
            //充值余额成功过后，在增加打码量
            //获取充值打码量倍率 充值增加打码量
            PlayMoneyVO playMoneyVO = new PlayMoneyVO();
            playMoneyVO.setPlayMoneyType(BackendConstants.INCOME);
            playMoneyVO.setId(vo.getId());
            playMoneyVO.setRemark(vo.getRemark());
            SysPlayMoneyParameterBO sysPlayMoneyParameterBO = playMoneyService.getSysPlayMoney();
            BigDecimal recharge = sysPlayMoneyParameterBO.getRecharge();
            playMoneyVO.setAmount(BigDecimal.valueOf(vo.getAmount().longValue()).multiply(recharge));
            ResponseEntity playMoneyResponseEntity = doIncomePlayMoney(playMoneyVO, orderId);
            if (playMoneyResponseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
                log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                        currentUser.getUserName() + "为用户id={" + vo.getId() + "}增加打码量成功", BackendConstants.USER_ASSETS_MODULE);
                return ResponseUtil.success();
            }
        } else {
            //删除订单
            doDeleteOrder(orderId);
        }
        return ResponseUtil.fail();
    }

    private ResponseEntity doCreateOrder(SsOrderAddVO order) {
        String url = userServerUrl + UserServerConstants.USER_ORDER_ADD;
        Map<String, Object> param = BackendServerUtil.objectToMap(order);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }

    private ResponseEntity doDeleteOrder(Long orderId) {
        String url = userServerUrl + UserServerConstants.USER_ORDER_DELETE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("id", orderId);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }

    private SsOrderAddVO chargeOrder(BalanceVO vo, Admin currentUser) {
        SsOrderAddVO ssOrder = new SsOrderAddVO();
        ssOrder.setUserId(vo.getId());
        ssOrder.setOrderType(OrderTypeEnum.CHARGE_ORDER.getCode());
        ssOrder.setOrderStatus(OrderStatusEnum.ORDER_SUCCESS.getCode());
        ssOrder.setAmount(new BigDecimal(vo.getAmount().intValue()));
        ssOrder.setRemark(currentUser.getUserName() + "为用户userId=" + vo.getId() + "充值" + vo.getAmount());
        return ssOrder;
    }

    private ResponseEntity doIncomePlayMoney(PlayMoneyVO vo, Long relateId) {
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_PLAY_MONEY;
        Map<String, Object> param = new HashMap<>(16);
        param.put("playMoneyType", BackendConstants.INCOME);
        param.put("userId", vo.getId());
        param.put("amount", vo.getAmount());
        param.put("remark", vo.getRemark());
        param.put("relateId", relateId);
        param.put("changeType", PlayMoneyChangeEnum.RECHARGE.getCode());
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }

    private ResponseEntity doIncomeBalance(BalanceVO vo, Long relateId) {
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_BALANCE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("balanceType", BackendConstants.INCOME);
        param.put("userId", vo.getId());
        param.put("amount", vo.getAmount());
        param.put("remark", vo.getRemark());
        param.put("relateId", relateId);
        param.put("changeType", BalanceChangeEnum.RECHARGE.getCode());
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }

    @ApiOperation(value = "用户下分")
    @PostMapping("reduceBalance")
    public ResponseEntity reduceBalance(BalanceVO vo) {
        if (null == vo.getId() || vo.getId() < 0 || null == vo.getAmount() || vo.getAmount() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        if (BackendServerUtil.checkIntAmount(vo.getAmount())) {
            return new ResponseEntity("金额不规范");
        }

        //获取个人资产
        ResponseEntity assetsResponse = findAssetsById(vo.getId());
        if (assetsResponse.getCode() != ResponseCode.SUCCESS.getCode()) {
            return assetsResponse;
        }
        UserAssetsBO userAssetsBO = JSONObject.parseObject(assetsResponse.getData().toString(), UserAssetsBO.class);
        if (userAssetsBO.getPlayMoney().compareTo(BigDecimal.ONE) >= 0) {
            return new ResponseEntity("不能下分，打码量不足");
        }
        Admin currentUser = commonService.getCurrentUser();
        SsOrderAddVO order = createWithdrawOrder(vo, currentUser);
        ResponseEntity orderResponseEntity = doCreateOrder(order);
        if (orderResponseEntity.getCode() != ResponseCode.SUCCESS.getCode()) {
            return ResponseUtil.fail();
        }
        SsOrderAddBO ssOrderAddBO = JSONObject.parseObject(JSONObject.toJSONString(orderResponseEntity.getData()),SsOrderAddBO.class);
        Long orderId = ssOrderAddBO.getId();
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                currentUser.getUserName() + "为用户id={" + vo.getId() + "}新增提现(下分)订单成功", BackendConstants.ORDER);

        //下分减去
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_BALANCE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("balanceType", BackendConstants.EXPENSES);
        param.put("userId", vo.getId());
        param.put("amount", vo.getAmount());
        param.put("remark", vo.getRemark());
        param.put("changeType", BalanceChangeEnum.WITHDRAW.getCode());
        param.put("relateId", orderId);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            //删除订单
            doDeleteOrder(orderId);
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    currentUser.getUserName() + "为用户id={" + vo.getId() + "}下分", BackendConstants.USER_ASSETS_MODULE);
        } else {
            //删除订单
            doDeleteOrder(orderId);
        }
        return responseEntity;
    }

    private SsOrderAddVO createWithdrawOrder(BalanceVO vo, Admin currentUser) {
        SsOrderAddVO ssOrder = new SsOrderAddVO();
        ssOrder.setUserId(vo.getId());
        ssOrder.setOrderType(OrderTypeEnum.WITHDRAW_ORDER.getCode());
        ssOrder.setOrderStatus(OrderStatusEnum.ORDER_SUCCESS.getCode());
        ssOrder.setAmount(new BigDecimal(vo.getAmount().intValue()));
        ssOrder.setRemark(currentUser.getUserName() + "为用户userId=" + vo.getId() + "提现" + vo.getAmount());
        return ssOrder;
    }


    //@ApiOperation(value = "用户个人资产")
    //@GetMapping("findAssetsById")
    public ResponseEntity<UserAssetsBO> getAssetsById(IdVO vo) {
        if (null == vo.getId() || vo.getId() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        return findAssetsById(vo.getId());
    }

    private ResponseEntity<UserAssetsBO> findAssetsById(Long id) {
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_BYID + "?userId=" + id;
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }


    @GetMapping("changeBalancePage")
    @ApiOperation(("用户余额变动记录分页"))
    public ResponseEntity<Page<UserBalanceChangePageBO>> changeBalancePage(UserChangeBalancePageVO vo) throws Exception {
        if (null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(userServerUrl + UserServerConstants.USERSERVER_ASSETS_CHANGE_BALANCE_PAGE);
        sb.append("?pageNumber=" + vo.getPageNumber() + "&pageSize=" + vo.getPageSize());
        sb.append("&userId=" + vo.getId());
        if (null != vo.getChangeType()) {
            sb.append("&changeType=" + vo.getChangeType());
        }
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sb.append("&startTime=" + URLEncoder.encode(vo.getStartTime().trim(), BackendConstants.UTF8));
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sb.append("&endTime=" + URLEncoder.encode(vo.getEndTime().trim(), BackendConstants.UTF8));
        }
        String url = sb.toString();
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (Objects.nonNull(responseEntity) && responseEntity.getCode() == 0) {
            JSONObject page = (JSONObject) responseEntity.getData();
            List<UserBalanceChangePageBO> list = JSONArray.parseArray(page.getString("content"), UserBalanceChangePageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                for (UserBalanceChangePageBO bo : list) {
                    setUserBalanceChangePageBo(bo);
                }
                page.put("content", list);
                responseEntity.setData(page);
            }
        }
        return responseEntity;
    }

    private void setUserBalanceChangePageBo(UserBalanceChangePageBO bo) {
        balanceType(bo);
        changeType(bo);
    }

    private void changeType(UserBalanceChangePageBO bo) {
        if (Objects.nonNull(bo.getChangeType())) {
            if (bo.getChangeType().equals(BalanceChangeEnum.RECHARGE.getCode())) {
                bo.setChangeTypeName(BalanceChangeEnum.RECHARGE.getName());
                return;
            }
            if (bo.getChangeType().equals(BalanceChangeEnum.BET.getCode())) {
                bo.setChangeTypeName(BalanceChangeEnum.BET.getName());
                return;
            }
            if (bo.getChangeType().equals(BalanceChangeEnum.WIN.getCode())) {
                bo.setChangeTypeName(BalanceChangeEnum.WIN.getName());
                return;
            }
        }
    }

    private void balanceType(UserBalanceChangePageBO bo) {
        if (Objects.nonNull(bo.getBalanceType())) {
            if (bo.getBalanceType() == BackendConstants.INCOME) {
                bo.setBalanceTypeName("收入");
            } else {
                bo.setBalanceTypeName("支出");
            }
        }
    }


    @GetMapping("changePlayMoneyPage")
    @ApiOperation(("用户打码量变动记录分页"))
    public ResponseEntity<Page<UserPlayMoneyChangePageBO>> changePlayMoneyPage(UserChangePlayMoneyPageVO vo) throws Exception {
        if (null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(userServerUrl + UserServerConstants.USERSERVER_ASSETS_CHANGE_PLAYMONEY_PAGE);
        sb.append("?pageNumber=" + vo.getPageNumber() + "&pageSize=" + vo.getPageSize());
        sb.append("&userId=" + vo.getId());
        if (null != vo.getChangeType()) {
            sb.append("&changeType=" + vo.getChangeType());
        }
        if (StringUtils.isNotEmpty(vo.getStartTime())) {
            sb.append("&startTime=" + URLEncoder.encode(vo.getStartTime().trim(), BackendConstants.UTF8));
        }
        if (StringUtils.isNotEmpty(vo.getEndTime())) {
            sb.append("&endTime=" + URLEncoder.encode(vo.getEndTime().trim(), BackendConstants.UTF8));
        }
        String url = sb.toString();
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (Objects.nonNull(responseEntity) && responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            JSONObject page = (JSONObject) responseEntity.getData();
            List<UserPlayMoneyChangePageBO> list = JSONArray.parseArray(page.getString("content"), UserPlayMoneyChangePageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                for (UserPlayMoneyChangePageBO bo : list) {
                    setUserPlayMoneyChangePageBo(bo);
                }
                page.put("content", list);
                responseEntity.setData(page);
            }
        }
        return responseEntity;
    }

    private void setUserPlayMoneyChangePageBo(UserPlayMoneyChangePageBO bo) {
        playMoneyType(bo);
        playMoneyChangeType(bo);
    }

    private void playMoneyType(UserPlayMoneyChangePageBO bo) {
        if (Objects.nonNull(bo.getChangeType())) {
            if (bo.getChangeType().equals(PlayMoneyChangeEnum.RECHARGE.getCode())) {
                bo.setChangeTypeName(PlayMoneyChangeEnum.RECHARGE.getName());
                return;
            }
            if (bo.getChangeType().equals(PlayMoneyChangeEnum.SETTLEMENT.getCode())) {
                bo.setChangeTypeName(PlayMoneyChangeEnum.SETTLEMENT.getName());
                return;
            }
        }
    }

    private void playMoneyChangeType(UserPlayMoneyChangePageBO bo) {
        if (Objects.nonNull(bo.getPlayMoneyType())) {
            if (bo.getPlayMoneyType() == BackendConstants.INCOME) {
                bo.setPlayMoneyTypeName("收入");
            } else {
                bo.setPlayMoneyTypeName("支出");
            }
        }
    }


}
