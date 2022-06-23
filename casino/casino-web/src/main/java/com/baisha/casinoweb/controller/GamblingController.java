package com.baisha.casinoweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.business.GamblingBusiness;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@RestController
@RequestMapping("/g")
@Api(tags = { "賭局s管理" })
@Slf4j
public class GamblingController {
	
	@Autowired
	private GamblingBusiness gamblingBusiness;
	
    @PostMapping("currentActive")
    @ApiOperation("当前局号")
    public ResponseEntity<String> currentActive(Long tgChatId) {

		log.info("[当前局号]");
        return ResponseUtil.success(gamblingBusiness.currentActive(tgChatId));
    }
	
    @PostMapping("limitStakes")
    @ApiOperation("限红")
    public ResponseEntity<String> limitStakes(Long tgChatId) {

		log.info("[限红]");
        return ResponseUtil.success(gamblingBusiness.limitStakes(tgChatId));
    }
	
}
