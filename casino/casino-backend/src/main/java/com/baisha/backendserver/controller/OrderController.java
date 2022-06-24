package com.baisha.backendserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.backendserver.response.BetResponse;
import com.baisha.backendserver.model.vo.bet.BetPageVO;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: alvin
 */
@RestController
@Api(tags = "订单管理")
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Value("${url.gameServer}")
    private String gameServerUrl;

    @PostMapping("page")
    @ApiOperation(("订单查询"))
    public ResponseEntity<Page<BetResponse>> page(BetPageVO betRequest) {
    	
    	log.info("订单查询");
    	

    	Map<String, Object> params = new HashMap<>();
    	params.put("userName", betRequest.getUserName());
    	params.put("betOption", betRequest.getBetOption());
    	params.put("noRun", betRequest.getNoRun());
    	params.put("noActive", betRequest.getNoActive());
    	params.put("status", betRequest.getStatus());
    	params.put("pageNumber", betRequest.getPageNumber());
    	params.put("pageSize", betRequest.getPageSize());

    	String result = HttpClient4Util.doPost(
    			gameServerUrl + GameServerConstants.ORDER_PAGE,
				params);
    	
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @GetMapping("betOption")
    @ApiOperation("下注类型")
    public ResponseEntity<List<Map<String, String>>> betOption() {
    	log.info("下注类型");
        return ResponseUtil.success(BetOption.getList()
        		.stream()
        		.map(option -> { 
        			Map<String, String> map = new HashMap<>();
        			map.put("name", option.getDisplay());
        			map.put("value", option.toString());
        			return map;
        		}).collect(Collectors.toList()));
    }
}
