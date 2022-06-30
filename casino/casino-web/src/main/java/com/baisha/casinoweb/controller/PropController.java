package com.baisha.casinoweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.core.dto.SysTelegramDto;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@RestController
@RequestMapping("prop")
@Api(tags = { "配置管理" })
@Slf4j
public class PropController {

    @Autowired
    private TelegramService telegramService;

    @PostMapping("systemTg")
    @ApiOperation("系统属性")
	@NoAuthentication
    public ResponseEntity<SysTelegramDto> systemTg() {
    	
		log.info("系统属性");
		
        return ResponseUtil.success(telegramService.getSysTelegram());
    }
}
