package com.baisha.backendserver.controller;

import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yihui
 */
@Slf4j
@Api(tags = "TG群管理")
@RestController
@RequestMapping(value = "tgGroup")
public class TgGroupController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;
    @Autowired
    private CommonBusiness commonService;

    @ApiOperation(value = "TG群分页")
    @GetMapping(value = "getTgGroupPage")
    public ResponseEntity getTgGroupPage() {

        return ResponseUtil.success();
    }


}
