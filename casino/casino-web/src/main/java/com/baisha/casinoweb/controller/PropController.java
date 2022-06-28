package com.baisha.casinoweb.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.model.vo.response.PropResponseVO;
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

    @PostMapping("map")
    @ApiOperation("系统属性")
	@NoAuthentication
    public ResponseEntity<PropResponseVO> finance() {
    	Map<Object, Object> sysTgMap = telegramService.getTelegramSet();
    	
		log.info("系统属性");
		PropResponseVO vo = new PropResponseVO();
		vo.setOnlyFinance((String) sysTgMap.get("onlyFinance"));
		vo.setOnlyCustomerService((String) sysTgMap.get("onlyCustomerService"));
		vo.setOfficialGamingChannel((String) sysTgMap.get("officialGamingChannel"));
		
        return ResponseUtil.success(vo);
    }
}
