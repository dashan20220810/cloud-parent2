package com.baisha.casinoweb.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.casinoweb.util.enums.TgImageEnum;
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
@RequestMapping("tg")
@Api(tags = { "telegram" })
@Slf4j
public class TgController {
	
//	@Value("${project.telegram.image-domain-and-bucket}")
//	private String imageDomainAndBucket;

    @Autowired
    private RedisUtil redisUtil;

//	@Autowired
//	private TgService tgService;

    @PostMapping("image")
    @ApiOperation("telegram image")
	@NoAuthentication
    public ResponseEntity<String> image( TgImageEnum tgImageEnum ) {
		log.info("[telegram image]");
    	
    	if ( tgImageEnum==null ) {
			log.info("telegram image检核失败");
			return ResponseUtil.parameterNotNull();
    	}
    	
//    	TgImage tgImage = tgService.findByTgImage(tgImageEnum.toString());
//    	if ( tgImage==null ) {
//            return ResponseUtil.fail();
//    	}

    	Map<Object, Object> sysTgMap = redisUtil.hmget(Constants.SYS_TELEGRAM);
    	String result = (String) sysTgMap.get(tgImageEnum.getKey());
    	
    	if ( StringUtils.isBlank(result) ) {
    		return ResponseUtil.fail();
    	}

		log.info("[telegram image] 成功");
//        return ResponseUtil.success( imageDomainAndBucket +"/" +tgImage.getName());
        return ResponseUtil.success( result );
    }
	
}
