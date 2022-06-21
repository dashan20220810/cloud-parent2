package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.constants.TgBotServerConstants;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.vo.StatusVO;
import com.baisha.backendserver.vo.tgBot.TgBotPageVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kimi
 */
@Api(tags = "机器人管理")
@RestController
@RequestMapping("tgBot")
public class TgBotController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;

    @Autowired
    private CommonService commonService;


    @ApiOperation("新开机器人")
    @ApiImplicitParams({@ApiImplicitParam(name = "username", value = "机器人名称", required = true),
            @ApiImplicitParam(name = "token", value = "机器人token", required = true), @ApiImplicitParam(name = "chatId"
            , value = "TG群id", required = true)})
    @PostMapping("open")
    public ResponseEntity open(String username, String token, String chatId) {
        // 参数校验
        if (CommonUtil.checkNull(username, token, chatId)) {
            return ResponseUtil.parameterNotNull();
        }
        // 后台登陆用户
        Admin current = commonService.getCurrentUser();
        String url = tgBotServerUrl + TgBotServerConstants.OPEN_TG_BOT;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", username);
        paramMap.put("token", token);
        paramMap.put("chatId", chatId);
        paramMap.put("createBy", current.getUserName());
        paramMap.put("updateBy", current.getUserName());
        String result = HttpClient4Util.doPost(url, paramMap);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation("分页查询")
    @PostMapping("page")
    public ResponseEntity page(TgBotPageVO vo) {
        String url = tgBotServerUrl + TgBotServerConstants.PAGE_TG_BOT;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation("更新状态 0禁用 1启用")
    @PostMapping("updateStatus")
    public ResponseEntity updateStatus(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // 参数校验
        if (CommonUtil.checkNull(id.toString(), status.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.UPDATE_STATUS_TG_BOT;
        Map<String, Object> param = BackendServerUtil.objectToMap(statusVO);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }
}
