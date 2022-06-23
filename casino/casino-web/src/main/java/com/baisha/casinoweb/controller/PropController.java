package com.baisha.casinoweb.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;

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
    private RedisUtil redisUtil;

    @PostMapping("finance")
    @ApiOperation("唯一财务")
	@NoAuthentication
    public ResponseEntity<String> finance() {
    	Map<Object, Object> sysTgMap = redisUtil.hmget(Constants.SYS_TELEGRAM);
    	
		log.info("[唯一财务]");
        return ResponseUtil.success(sysTgMap.get("onlyFinance"));
    }

    @PostMapping("customer")
    @ApiOperation("客服")
	@NoAuthentication
    public ResponseEntity<String> customerService() {
    	Map<Object, Object> sysTgMap = redisUtil.hmget(Constants.SYS_TELEGRAM);

		log.info("[客服]");
        return ResponseUtil.success(sysTgMap.get("onlyCustomerService"));
    }
}