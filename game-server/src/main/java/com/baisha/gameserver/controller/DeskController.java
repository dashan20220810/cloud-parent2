package com.baisha.gameserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.model.Desk;
import com.baisha.gameserver.service.DeskService;
import com.baisha.gameserver.vo.DeskVO;
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

    @PostMapping("queryById")
    @ApiOperation("桌台查询-id")
    public ResponseEntity<Desk> queryById( Long tableId ) {
    	
    	log.info("桌台查询-id");
    	Desk result = deskService.findById(tableId);
    	if ( result==null ) {
			log.warn("桌台查询-id 失敗");
            return ResponseUtil.fail();
    	}
    	
        return ResponseUtil.success(result);
    }

    @PostMapping("add")
    @ApiOperation("桌台新增")
    public ResponseEntity<String> add(DeskVO deskVO) {

		log.info("[桌台新增] ");
    	Desk desk = deskVO.generateDesk();
    	
    	if ( Desk.checkRequest(desk)==false ) {
    		log.info("[桌台新增] 检核失败");
    		return ResponseUtil.fail();
    	}
    	
    	deskService.save(desk);

		log.info("[桌台新增] 成功!");
    	return ResponseUtil.success();
    }
    
}
