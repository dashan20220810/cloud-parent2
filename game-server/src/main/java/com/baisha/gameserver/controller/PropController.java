package com.baisha.gameserver.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.util.enums.RedisPropEnum;
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
@Api(tags = "gs配置管理")
@RequestMapping("prop")
@Slf4j
public class PropController {

    @Value("${project.game.return-amount-multiplier}")
    private BigDecimal gameReturnAmountMultiplier;

    @Autowired
    private RedisUtil redisUtil;
    

    @GetMapping("queryReturnAmountMultiplier")
    @ApiOperation("查询返水比例")
    public ResponseEntity<String> queryReturnAmountMultiplier() {

    	log.info("查询返水比例");
        BigDecimal returnAmountMultiplier = redisUtil.getValue(RedisPropEnum.ReturnAmountMultiplier.getKey());
        if (returnAmountMultiplier == null) {
        	returnAmountMultiplier = gameReturnAmountMultiplier;
        	redisUtil.setValue(RedisPropEnum.ReturnAmountMultiplier.getKey(), returnAmountMultiplier);
        }
        
        return ResponseUtil.success(returnAmountMultiplier.toString());
    }

    @PostMapping("updateReturnAmountMultiplier")
    @ApiOperation("修改返水比例")
    public ResponseEntity<String> updateReturnAmountMultiplier(BigDecimal returnAmountMultiplier) {

    	log.info("修改返水比例");
    	if(returnAmountMultiplier == null || returnAmountMultiplier.compareTo(BigDecimal.ZERO) == -1) {
        	log.warn("修改返水比例 参数不得为空或负数");
        	return ResponseUtil.fail();
    	}
    	redisUtil.setValue(RedisPropEnum.ReturnAmountMultiplier.getKey(), returnAmountMultiplier);
        
        return ResponseUtil.success();
    }
}
