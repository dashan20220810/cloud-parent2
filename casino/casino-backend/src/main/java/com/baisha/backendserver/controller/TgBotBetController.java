package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.order.BetPageBO;
import com.baisha.backendserver.model.bo.tgBot.TgBotAutoPageBO;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.StatusVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotAutoAddVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotAutoPageVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotAutoUpdateVO;
import com.baisha.backendserver.model.vo.user.UserSaveVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.TgBotServerConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.enums.TgBaccRuleEnum;
import com.baisha.modulecommon.enums.UserOriginEnum;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "???????????????(????????????)")
@RestController
@RequestMapping("autoTgBot")
public class TgBotBetController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;
    @Value("${url.userServer}")
    private String userServerUrl;
    @Autowired
    private CommonBusiness commonService;


    @GetMapping("betOption")
    @ApiOperation("??????????????????")
    public ResponseEntity<List<Map<String, String>>> betOption() {
        return ResponseUtil.success(BetOption.getList()
                .stream()
                .map(option -> {
                    Map<String, String> map = new HashMap<>(16);
                    //?????? - ????????????
                    map.put("name", option.getDisplay());
                    //??? - ????????????
                    map.put("value", option.toString());
                    return map;
                }).collect(Collectors.toList()));
    }

    @ApiOperation("???????????????")
    @PostMapping("addBetBot")
    public synchronized ResponseEntity addBetBot(TgBotAutoAddVO vo) throws IllegalAccessException {
        // ????????????
        if (!CommonUtil.checkObjectFieldNotNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (TgBotAutoAddVO.checkTgUserId(vo.getBetBotId())) {
            return new ResponseEntity("TG??????ID??????");
        }

        //?????? ????????????
        int maxBetFrequency = 10;
        //????????????-???????????? 1-5??????
        int maxMinMultiple = 5;
        //????????????-???????????? 6-20??????
        int maxMaxMultiple = 20;
        if (vo.getBetFrequency() < 0 || vo.getBetFrequency() > maxBetFrequency
                || vo.getMinMultiple() <= 0 || vo.getMinMultiple() > maxMinMultiple
                || vo.getMinMultiple() > vo.getMaxMultiple()
                || vo.getMaxMultiple() <= maxMinMultiple || vo.getMaxMultiple() > maxMaxMultiple) {
            return new ResponseEntity("???????????????");
        }

        // ??????????????????
        Admin current = commonService.getCurrentUser();
        //?????????????????????user???????????????????????????
        boolean userFlag = doSaveUserAsBot(vo);
        if (!userFlag) {
            log.error("??????????????????user?????????????????????????????????");
            return ResponseUtil.fail();
        }

        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_ADDBETBOT;
        Map<String, Object> paramMap = BackendServerUtil.objectToMap(vo);
        log.info("???????????????(????????????)-?????? paramMap =  {}", JSONObject.toJSONString(paramMap));
        String result = HttpClient4Util.doPost(url, paramMap);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", current.getUserName(), BackendConstants.INSERT,
                    JSON.toJSONString(paramMap), BackendConstants.TOBOT_AUTO_MODULE);
        }
        return responseEntity;
    }

    private boolean doSaveUserAsBot(TgBotAutoAddVO vo) {
        UserSaveVO userSaveVO = new UserSaveVO();
        userSaveVO.setUserType(UserTypeEnum.BOT.getCode());
        //???????????????????????????????????????
        userSaveVO.setOrigin(UserOriginEnum.TG_ORIGIN.getOrigin());
        userSaveVO.setTgUserId(vo.getBetBotId());
        userSaveVO.setPhone(vo.getBetBotPhone());
        userSaveVO.setUserName(vo.getBetBotId());
        userSaveVO.setTgGroupId(BackendConstants.DEFAULT_TG_GROUP_ID);
        userSaveVO.setTgGroupName(BackendConstants.DEFAULT_TG_GROUP_NAME);
        userSaveVO.setNickName(vo.getBetBotName());

        String url = userServerUrl + UserServerConstants.USERSERVER_USER_SAVE;
        Map<String, Object> param = BackendServerUtil.objectToMap(userSaveVO);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            log.error("??????????????????-1");
            return false;
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            //??????????????? ??????????????? ?????????????????????????????????????????????
            url = userServerUrl + UserServerConstants.USERSERVER_USER_TYPE;
            param = new HashMap<>(16);
            //????????? ???TG??????ID
            param.put("userName", vo.getBetBotId());
            param.put("userType", UserTypeEnum.BOT.getCode());
            result = HttpClient4Util.doPost(url, param);
            if (CommonUtil.checkNull(result)) {
                log.error("????????????????????????");
                return false;
            }
        } else {
            log.error("??????????????????-2");
            return false;
        }

        return true;
    }

    @ApiOperation("???????????????")
    @PostMapping("updateBetBot")
    public ResponseEntity updateBetBot(TgBotAutoUpdateVO vo) throws IllegalAccessException {
        // ????????????
        if (!CommonUtil.checkObjectFieldNotNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        //?????? ????????????
        int maxBetFrequency = 10;
        //????????????-???????????? 1-5??????
        int maxMinMultiple = 5;
        //????????????-???????????? 6-20??????
        int maxMaxMultiple = 20;
        if (vo.getBetFrequency() < 0 || vo.getBetFrequency() > maxBetFrequency
                || vo.getMinMultiple() < 0 || vo.getMinMultiple() > maxMinMultiple
                || vo.getMinMultiple() > vo.getMaxMultiple()
                || vo.getMaxMultiple() <= maxMinMultiple || vo.getMaxMultiple() > maxMaxMultiple) {
            return new ResponseEntity("???????????????");
        }

        // ??????????????????
        Admin current = commonService.getCurrentUser();
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_ADDBETBOT;
        Map<String, Object> paramMap = BackendServerUtil.objectToMap(vo);
        log.info("???????????????(????????????)-?????? paramMap = {}", JSONObject.toJSONString(paramMap));
        String result = HttpClient4Util.doPost(url, paramMap);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", current.getUserName(), BackendConstants.UPDATE,
                    JSON.toJSONString(paramMap), BackendConstants.TOBOT_AUTO_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation("????????????")
    @PostMapping("updateStatus")
    public ResponseEntity updateStatus(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // ????????????
        if (CommonUtil.checkNull(id.toString(), status.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_UPDATESTATUSBYID;
        Map<String, Object> param = BackendServerUtil.objectToMap(statusVO);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin current = commonService.getCurrentUser();
            log.info("{} {} {} {}", current.getUserName(), BackendConstants.UPDATE,
                    JSON.toJSONString(statusVO), BackendConstants.TOBOT_AUTO_MODULE);
        }
        return responseEntity;
    }

    @ApiOperation("???????????????")
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (null == vo.getId() || vo.getId().intValue() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_DELBOT;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin current = commonService.getCurrentUser();
            log.info("{} {} {} {}", current.getUserName(), BackendConstants.DELETE, "???????????????" + JSON.toJSONString(vo)
                    , BackendConstants.TOBOT_AUTO_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation("????????????")
    @GetMapping("page")
    public ResponseEntity<Page<TgBotAutoPageBO>> page(TgBotAutoPageVO vo) {
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_PAGE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (Objects.nonNull(responseEntity) && responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            JSONObject page = (JSONObject) responseEntity.getData();
            List<TgBotAutoPageBO> list = JSONArray.parseArray(page.getString("content"), TgBotAutoPageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                for (TgBotAutoPageBO bo : list) {
                    setTgBotAutoPageBO(bo);
                }
                page.put("content", list);
                responseEntity.setData(page);
            }
        }
        return responseEntity;
    }

    private void setTgBotAutoPageBO(TgBotAutoPageBO bo) {
        String betContents = bo.getBetContents();
        String[] betContentsArr = betContents.split(",");
        String name = "";
        for (String option : betContentsArr) {
            BetOption betOption = BetOption.getBetOption(option);
            if (Objects.nonNull(betOption)) {
                name = name + betOption.getDisplay() + ",";
            }
        }
        if (StringUtils.isNotEmpty(name)) {
            name = name.substring(0, name.length() - 1);
            bo.setBetContentsName(name);
        } else {
            bo.setBetContentsName("????????????");
        }
    }


}
