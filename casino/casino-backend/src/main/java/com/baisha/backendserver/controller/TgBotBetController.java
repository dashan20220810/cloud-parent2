package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.TgBotServerConstants;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags = "机器人管理(自动投注)")
@RestController
@RequestMapping("autoTgBot")
public class TgBotBetController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;
    @Autowired
    private CommonBusiness commonService;

    @ApiOperation("新增机器人")
    @PostMapping("addBetBot")
    public ResponseEntity open(String username, String token) {
        // 参数校验
        if (CommonUtil.checkNull(username, token)) {
            return ResponseUtil.parameterNotNull();
        }
        // 后台登陆用户
        Admin current = commonService.getCurrentUser();
        String url = tgBotServerUrl + TgBotServerConstants.OPEN_TG_BOT;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", username);
        paramMap.put("token", token);
        String result = HttpClient4Util.doPost(url, paramMap);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            log.info("{} {} {} {}", current.getUserName(), BackendConstants.INSERT,
                    JSON.toJSONString(paramMap), BackendConstants.TOBOT_MODULE);
        }
        return responseEntity;
    }


}
