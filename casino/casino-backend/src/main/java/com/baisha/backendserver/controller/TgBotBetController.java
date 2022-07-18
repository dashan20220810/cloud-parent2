package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.tgBot.TgBotAutoPageBO;
import com.baisha.backendserver.model.bo.tgBot.TgBotPageBO;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.StatusVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotAutoAddVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotAutoPageVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotAutoUpdateVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotPageVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.TgBotServerConstants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.reponse.ResponseCode;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = "机器人管理(自动投注)")
@RestController
@RequestMapping("autoTgBot")
public class TgBotBetController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;
    @Autowired
    private CommonBusiness commonService;


    @GetMapping("betOption")
    @ApiOperation("投注内容 列表")
    public ResponseEntity<List<Map<String, String>>> betOption() {
        return ResponseUtil.success(BetOption.getList()
                .stream()
                .map(option -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", option.getDisplay());
                    map.put("value", option.toString());
                    return map;
                }).collect(Collectors.toList()));
    }

    @ApiOperation("新增机器人")
    @PostMapping("addBetBot")
    public ResponseEntity addBetBot(TgBotAutoAddVO vo) throws IllegalAccessException {
        // 参数校验
        if (!CommonUtil.checkObjectFieldNotNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        int maxBetFrequency = 10;
        int maxMinMultiple = 5;
        int maxMaxMultiple = 20;
        if (vo.getBetFrequency() < 0 || vo.getBetFrequency() > maxBetFrequency
                || vo.getMinMultiple() <= 0 || vo.getMinMultiple() > maxMinMultiple
                || vo.getMinMultiple() > vo.getMaxMultiple()
                || vo.getMaxMultiple() <= 0 || vo.getMaxMultiple() > maxMaxMultiple) {
            return new ResponseEntity("数据不规范");
        }

        // 后台登陆用户
        Admin current = commonService.getCurrentUser();
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_ADDBETBOT;
        Map<String, Object> paramMap = BackendServerUtil.objectToMap(vo);
        log.info("机器人管理(自动投注)-新增 paramMap =  {}", JSONObject.toJSONString(paramMap));
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

    @ApiOperation("修改机器人")
    @PostMapping("updateBetBot")
    public ResponseEntity updateBetBot(TgBotAutoUpdateVO vo) throws IllegalAccessException {
        // 参数校验
        if (!CommonUtil.checkObjectFieldNotNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        int maxBetFrequency = 100;
        int maxMinMultiple = 5;
        int maxMaxMultiple = 20;
        if (vo.getBetFrequency() < 0 || vo.getBetFrequency() > maxBetFrequency || vo.getId() <= 0
                || vo.getMinMultiple() < 0 || vo.getMinMultiple() > maxMinMultiple
                || vo.getMinMultiple() > vo.getMaxMultiple()
                || vo.getMaxMultiple() < 0 || vo.getMaxMultiple() > maxMaxMultiple) {
            return new ResponseEntity("数据不规范");
        }

        // 后台登陆用户
        Admin current = commonService.getCurrentUser();
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_ADDBETBOT;
        Map<String, Object> paramMap = BackendServerUtil.objectToMap(vo);
        log.info("机器人管理(自动投注)-编辑 paramMap = {}", JSONObject.toJSONString(paramMap));
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


    @ApiOperation("更新状态")
    @PostMapping("updateStatus")
    public ResponseEntity updateStatus(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // 参数校验
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

    @ApiOperation("机器人删除")
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
            log.info("{} {} {} {}", current.getUserName(), BackendConstants.DELETE, "删除机器人" + JSON.toJSONString(vo)
                    , BackendConstants.TOBOT_AUTO_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation("分页查询")
    @GetMapping("page")
    public ResponseEntity<Page<TgBotAutoPageBO>> page(TgBotAutoPageVO vo) {
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_PAGE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }


}