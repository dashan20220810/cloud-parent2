package com.baisha.casinoweb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    @PostMapping("finance")
    @ApiOperation("唯一财务")
    public ResponseEntity<String> finance() {

		log.info("[唯一财务]");
        return ResponseUtil.success("@test_finance");
    }

    @PostMapping("customer")
    @ApiOperation("客服")
    public ResponseEntity<String> customerService() {

		log.info("[客服]");
        return ResponseUtil.success("@test_customerService");
    }
}
