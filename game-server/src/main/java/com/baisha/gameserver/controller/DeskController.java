package com.baisha.gameserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.Desk;
import com.baisha.gameserver.service.DeskService;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("desk")
@Api(tags = { "桌台控制器" })
@Slf4j
public class DeskController {

	@Autowired
	private DeskService deskService;

    @GetMapping("all")
    @ApiOperation("桌台查询-all")
    public ResponseEntity<List<Desk>> all() {
    	
    	log.info("桌台查询-all");
        return ResponseUtil.success(deskService.findAllDeskList());
    }

    @PostMapping("queryByLocalIp")
    @ApiOperation("桌台查询-LOCAL IP")
    public ResponseEntity<Desk> queryByLocalIp( String localIp ) {
    	
    	log.info("桌台查询-LOCAL IP");
    	Desk result = deskService.findByLocalIp(localIp);
    	if ( result==null ) {
			log.warn("桌台查询-LOCAL IP 失敗");
            return ResponseUtil.fail();
    	}
    	
        return ResponseUtil.success(result);
    }

    @PostMapping("queryByDeskCode")
    @ApiOperation("桌台查询-DeskCode")
    public ResponseEntity<Desk> queryByDeskCode( String deskCode ) {
    	
    	log.info("桌台查询-DeskCode");
    	Desk result = deskService.findByDeskCode(deskCode);
    	if ( result==null ) {
			log.warn("桌台查询-DeskCode 失敗");
            return ResponseUtil.fail();
    	}
    	
        return ResponseUtil.success(result);
    }
    
    
    
}
