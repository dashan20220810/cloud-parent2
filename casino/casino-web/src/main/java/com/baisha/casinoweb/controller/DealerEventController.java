package com.baisha.casinoweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.business.DealerBusiness;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("dealer")
@Api(tags = { "荷官端事件控制器" })
@Slf4j
public class DealerEventController {

	@Autowired
	private DealerBusiness dealerBusiness;

	/**
	 * 游戏开局
	 * @return
	 */
	@PostMapping("openNewGame")
	@ApiOperation("游戏开局")
	@NoAuthentication
	public ResponseEntity<String> openNewGame () {

		log.info("游戏开局");
		boolean result = dealerBusiness.openNewGame();
//		boolean result = false;
		if ( result==false ) {
			log.info("[游戏开局] 失敗");
            return ResponseUtil.fail();
		}

		log.info("[游戏开局] 成功");
        return ResponseUtil.success();
	}
	
}
