package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.business.PlayMoneyBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.BetStatistics;
import com.baisha.backendserver.model.bo.CodeNameBO;
import com.baisha.backendserver.model.bo.assets.OrderAdjustmentTypeBO;
import com.baisha.backendserver.model.bo.order.SsOrderAddBO;
import com.baisha.backendserver.model.bo.sys.SysPlayMoneyParameterBO;
import com.baisha.backendserver.model.bo.user.*;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.order.SsOrderAddVO;
import com.baisha.backendserver.model.vo.user.*;
import com.baisha.backendserver.service.BetStatisticsService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
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
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping("user")
@Api(tags = "????????????")
public class UserController {

    @Value("${url.userServer}")
    private String userServerUrl;
    @Autowired
    private CommonBusiness commonService;
    @Autowired
    private PlayMoneyBusiness playMoneyService;
    @Autowired
    private BetStatisticsService betStatisticsService;

    @GetMapping("page")
    @ApiOperation(("????????????"))
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

        ResponseEntity userResponse = JSONObject.parseObject(result, ResponseEntity.class);
        //??????????????????
        if (Objects.nonNull(userResponse) && userResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            JSONObject page = (JSONObject) userResponse.getData();
            List<UserPageBO> list = JSONArray.parseArray(page.getString("content"), UserPageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                for (UserPageBO u : list) {
                    if (StringUtils.isEmpty(u.getInviteTgUserName()) && StringUtils.isNotEmpty(u.getInviteTgUserId())) {
                        u.setInviteTgUserName(u.getInviteTgUserId());
                    }
                    if (null == u.getUserType()) {
                        u.setUserTypeName(UserTypeEnum.NORMAL.getName());
                    } else {
                        UserTypeEnum userTypeEnum = UserTypeEnum.nameOfCode(u.getUserType());
                        u.setUserTypeName(userTypeEnum.getName());
                    }
                    if (StringUtils.isEmpty(u.getChannelCode())) {
                        //???????????????
                        u.setChannelName("??????");
                    }
                    //??????????????????
                    BetStatistics betStatistics = betStatisticsService.findByUserId(u.getId());
                    if (Objects.nonNull(betStatistics)) {
                        u.setBetNum(betStatistics.getBetNum());
                        u.setBetAmount(betStatistics.getBetAmount());
                        u.setWinAmount(betStatistics.getWinAmount());
                        u.setLastBetTime(betStatistics.getLastBetTime());
                    }
                }
                page.put("content", list);
                userResponse.setData(page);
            }
        }

        return userResponse;
    }


    @ApiOperation(value = "????????????")
    @GetMapping("getUserType")
    public ResponseEntity<List<CodeNameBO>> getUserType() {
        List<UserTypeEnum> userType = UserTypeEnum.getList();
        List<CodeNameBO> list = userType.stream()
                .map(item -> CodeNameBO.builder()
                        .code(String.valueOf(item.getCode())).name(item.getName()).build())
                .filter(item -> Integer.parseInt(item.getCode()) != UserTypeEnum.BOT.getCode())
                .toList();
        return ResponseUtil.success(list);
    }

    @ApiOperation(value = "??????????????????")
    @PostMapping("updateUserType")
    public ResponseEntity updateUserType(UserTypeVO vo) {
        if (StringUtils.isEmpty(vo.getUserName()) || null == vo.getUserType() || vo.getUserType() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USER_TYPE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("userName", vo.getUserName());
        param.put("userType", vo.getUserType());
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            log.error("????????????????????????");
            return ResponseUtil.fail();
        }

        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    currentUser.getUserName() + "??????????????????" + JSONObject.toJSONString(param) + "", BackendConstants.USER_MODULE);
        }
        return responseEntity;
    }

    @ApiOperation(("??????/????????????"))
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
                    currentUser.getUserName() + "??????????????????id={" + vo.getId() + "}", BackendConstants.USER_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation(value = "????????????")
    @PostMapping("increaseBalance")
    public ResponseEntity increaseBalance(BalanceVO vo) {
        if (null == vo.getId() || vo.getId() < 0
                || null == vo.getAdjustmentType()
                || StringUtils.isEmpty(vo.getTgUserId())
                || null == vo.getAmount() || vo.getAmount() <= 0) {
            return ResponseUtil.parameterNotNull();
        }
        if (null == vo.getFlowMultiple() || vo.getFlowMultiple().compareTo(BigDecimal.ZERO) < 0
                || vo.getFlowMultiple().compareTo(new BigDecimal("100")) > 0) {
            return new ResponseEntity("?????????????????????");
        }
        if (BackendServerUtil.checkIntAmount(vo.getAmount())) {
            return new ResponseEntity("???????????????");
        }

        UserAssetsBO userAssetsBO = findAssetsTgUserId(vo.getTgUserId());
        if (Objects.isNull(userAssetsBO)) {
            return new ResponseEntity("?????????????????????");
        }
        vo.setId(userAssetsBO.getUserId());

        //????????????????????????
        Admin currentUser = commonService.getCurrentUser();
        //????????????
        SsOrderAddVO order = chargeOrder(vo, currentUser);
        ResponseEntity orderResponseEntity = doCreateOrder(order);
        if (orderResponseEntity.getCode() != ResponseCode.SUCCESS.getCode()) {
            return ResponseUtil.fail();
        }
        SsOrderAddBO ssOrderAddBO = JSONObject.parseObject(JSONObject.toJSONString(orderResponseEntity.getData()), SsOrderAddBO.class);
        Long orderId = ssOrderAddBO.getId();
        //????????????
        ResponseEntity balanceResponseEntity = doIncomeBalance(vo, orderId);
        if (balanceResponseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    currentUser.getUserName() + "?????????id={" + vo.getId() + "}????????????????????????", BackendConstants.ORDER_MODULE);

            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    currentUser.getUserName() + "?????????id={" + vo.getId() + "}??????????????????", BackendConstants.USER_ASSETS_MODULE);
            //?????????????????????????????????????????????
            //???????????????????????????  ????????????  ?????????????????????
            BigDecimal playMoney = BigDecimal.valueOf(vo.getAmount().longValue()).multiply(vo.getFlowMultiple());
            if (playMoney.compareTo(BigDecimal.ZERO) > 0) {
                PlayMoneyVO playMoneyVO = new PlayMoneyVO();
                playMoneyVO.setPlayMoneyType(BackendConstants.INCOME);
                playMoneyVO.setId(vo.getId());
                playMoneyVO.setRemark(vo.getRemark());
                playMoneyVO.setAmount(playMoney);
                ResponseEntity playMoneyResponseEntity = doIncomePlayMoney(playMoneyVO, orderId);
                if (playMoneyResponseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
                    log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                            currentUser.getUserName() + "?????????id={" + vo.getId() + "}?????????????????????", BackendConstants.USER_ASSETS_MODULE);
                }
            }
            return ResponseUtil.success();
        } else {
            //????????????
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
        ssOrder.setOrderType(OrderTypeEnum.CHARGE_ORDER.getCode());
        ssOrder.setOrderStatus(OrderStatusEnum.ORDER_SUCCESS.getCode());
        ssOrder.setUserId(vo.getId());
        ssOrder.setTgUserId(vo.getTgUserId());
        ssOrder.setAmount(new BigDecimal(vo.getAmount().intValue()));
        ssOrder.setFlowMultiple(vo.getFlowMultiple());
        ssOrder.setAdjustmentType(vo.getAdjustmentType());
        ssOrder.setFileKey(vo.getFileKey());
        if (StringUtils.isEmpty(vo.getRemark())) {
            ssOrder.setRemark(currentUser.getUserName() + "?????????userId=" + vo.getId() + "??????" + vo.getAmount());
        } else {
            ssOrder.setRemark(vo.getRemark());
        }
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

    @ApiOperation(value = "????????????")
    @PostMapping("reduceBalance")
    public ResponseEntity reduceBalance(BalanceSubVO vo) {
        if (null == vo.getId() || vo.getId() < 0
                || null == vo.getAdjustmentType()
                || StringUtils.isEmpty(vo.getTgUserId())
                || null == vo.getAmount() || vo.getAmount().intValue() <= 0) {
            return ResponseUtil.parameterNotNull();
        }
        if (BackendServerUtil.checkIntAmount(vo.getAmount().intValue())) {
            return new ResponseEntity("???????????????");
        }

        //??????????????????
        UserAssetsBO userAssetsBO = findAssetsTgUserId(vo.getTgUserId());
        if (Objects.isNull(userAssetsBO)) {
            return new ResponseEntity("?????????????????????");
        }
        vo.setId(userAssetsBO.getUserId());
       /* if (null != userAssetsBO.getUserType() && userAssetsBO.getUserType().equals(UserTypeEnum.BOT.getCode())) {
            return new ResponseEntity("?????????????????????(BOT)");
        }*/
        if (userAssetsBO.getPlayMoney().compareTo(BigDecimal.ONE) >= 0) {
            return new ResponseEntity("??????????????????????????????????????????????????????");
        }
        if (vo.getAmount().compareTo(userAssetsBO.getBalance()) > 0) {
            //??????????????????????????????????????????????????????????????????????????????????????????????????????
            vo.setAmount(userAssetsBO.getBalance());
        }

        Admin currentUser = commonService.getCurrentUser();
        SsOrderAddVO order = createWithdrawOrder(vo, currentUser, userAssetsBO);
        ResponseEntity orderResponseEntity = doCreateOrder(order);
        if (orderResponseEntity.getCode() != ResponseCode.SUCCESS.getCode()) {
            return ResponseUtil.fail();
        }
        SsOrderAddBO ssOrderAddBO = JSONObject.parseObject(JSONObject.toJSONString(orderResponseEntity.getData()), SsOrderAddBO.class);
        Long orderId = ssOrderAddBO.getId();

        //????????????
        String url = userServerUrl + UserServerConstants.USERSERVER_ASSETS_BALANCE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("balanceType", BackendConstants.EXPENSES);
        param.put("userId", vo.getId());
        param.put("amount", vo.getAmount().setScale(2, RoundingMode.DOWN));
        param.put("remark", vo.getRemark());
        param.put("changeType", BalanceChangeEnum.WITHDRAW.getCode());
        param.put("relateId", orderId);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            //????????????
            doDeleteOrder(orderId);
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    currentUser.getUserName() + "?????????id={" + vo.getId() + "}????????????(??????)????????????", BackendConstants.ORDER_MODULE);
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    currentUser.getUserName() + "?????????id={" + vo.getId() + "}??????", BackendConstants.USER_ASSETS_MODULE);
        } else {
            //????????????
            doDeleteOrder(orderId);
        }
        return responseEntity;
    }

    private SsOrderAddVO createWithdrawOrder(BalanceSubVO vo, Admin currentUser, UserAssetsBO userAssetsBO) {
        SsOrderAddVO ssOrder = new SsOrderAddVO();
        ssOrder.setOrderType(OrderTypeEnum.WITHDRAW_ORDER.getCode());
        ssOrder.setOrderStatus(OrderStatusEnum.ORDER_SUCCESS.getCode());
        ssOrder.setUserId(vo.getId());
        ssOrder.setTgUserId(vo.getTgUserId());
        ssOrder.setAmount(new BigDecimal(vo.getAmount().intValue()));
        ssOrder.setAdjustmentType(vo.getAdjustmentType());
        ssOrder.setFileKey(vo.getFileKey());
        if (StringUtils.isEmpty(vo.getRemark())) {
            ssOrder.setRemark(currentUser.getUserName() + "?????????" + userAssetsBO.getUserName() + "??????" + vo.getAmount());
        } else {
            ssOrder.setRemark(vo.getRemark());
        }
        return ssOrder;
    }


    //@ApiOperation(value = "??????????????????")
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

    @GetMapping("changeBalancePage")
    @ApiOperation(("??????????????????????????????"))
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
            BalanceChangeEnum balanceChangeEnum = BalanceChangeEnum.nameOfCode(bo.getChangeType());
            if (Objects.nonNull(balanceChangeEnum)) {
                bo.setChangeTypeName(balanceChangeEnum.getName());
            }
        }
    }

    private void balanceType(UserBalanceChangePageBO bo) {
        if (Objects.nonNull(bo.getBalanceType())) {
            if (bo.getBalanceType() == BackendConstants.INCOME) {
                bo.setBalanceTypeName("??????");
            } else {
                bo.setBalanceTypeName("??????");
            }
        }
    }


    @GetMapping("changePlayMoneyPage")
    @ApiOperation(("?????????????????????????????????"))
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
            PlayMoneyChangeEnum playMoneyChangeEnum = PlayMoneyChangeEnum.nameOfCode(bo.getChangeType());
            if (Objects.nonNull(playMoneyChangeEnum)) {
                bo.setChangeTypeName(playMoneyChangeEnum.getName());
            }
        }
    }

    private void playMoneyChangeType(UserPlayMoneyChangePageBO bo) {
        if (Objects.nonNull(bo.getPlayMoneyType())) {
            if (bo.getPlayMoneyType() == BackendConstants.INCOME) {
                bo.setPlayMoneyTypeName("??????");
            } else {
                bo.setPlayMoneyTypeName("??????");
            }
        }
    }


    @GetMapping(value = "balanceChangeType")
    @ApiOperation("??????????????????????????????")
    public ResponseEntity<List<ChangeTypeNameBO>> getBalanceChangeType() {
        return ResponseUtil.success(BalanceChangeEnum.getList()
                .stream()
                .map(option -> {
                            ChangeTypeNameBO bo = null;
                            //???????????????????????????????????????????????????
                            int code = option.getCode().intValue();
                            //????????????
                            int maxNum = 6;
                            if (code < maxNum) {
                                bo = ChangeTypeNameBO.builder().changeType(option.getCode())
                                        .changeTypeName(option.getName()).build();
                            }
                            return bo;
                        }
                ).filter(option -> Objects.nonNull(option)).collect(Collectors.toList()));
    }

    @GetMapping(value = "playMoneyChangeType")
    @ApiOperation("?????????????????????????????????")
    public ResponseEntity<List<ChangeTypeNameBO>> getPlayMoneyChangeType() {
        return ResponseUtil.success(PlayMoneyChangeEnum.getList()
                .stream()
                .map(option -> {
                            ChangeTypeNameBO bo = null;
                            //???????????????????????????????????????????????????
                            int code = option.getCode().intValue();
                            //????????????
                            int maxNum = 3;
                            if (code < maxNum) {
                                bo = ChangeTypeNameBO.builder().changeType(option.getCode())
                                        .changeTypeName(option.getName()).build();
                            }
                            return bo;
                        }
                ).filter(option -> Objects.nonNull(option)).collect(Collectors.toList()));
    }


}
