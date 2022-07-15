package com.baisha.gameserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.BetResult;
import com.baisha.gameserver.model.BetResultChange;
import com.baisha.gameserver.service.BetResultChangeService;
import com.baisha.gameserver.service.BetResultService;
import com.baisha.gameserver.vo.BetResultChangePageVO;
import com.baisha.gameserver.vo.BetResultChangeVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.PageUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


/**
 * @author: alvin
 */
@RestController
@Api(tags = "重开牌结果管理")
@RequestMapping("betResultChange")
@Slf4j
public class BetResultChangeController {

    @Autowired
    BetResultChangeService betResultChangeService;
    
    @Autowired
    BetResultService betResultService;

    @PostMapping("add")
    @ApiOperation("重开牌结果新增")
    public ResponseEntity<String> add(BetResultChangeVO request) {

        log.info("[重开牌结果] ");
        BetResultChange betResultChange = request.generateBetResultChange();

        if (betResultChange.checkRequest() == false) {
            log.info("[重开牌结果] 检核失败");
            return ResponseUtil.custom("请求错误");
        }

        BetResultChange brc = betResultChangeService.findCurrentByNoActive(request.getNoActive());
        if (brc!=null) {
        	betResultChange.setAwardOption(brc.getFinalAwardOption());
        } else {
        	BetResult betResult = betResultService.findByNoActive(request.getNoActive());
        	
        	if (betResult==null) {
        		log.warn("查无开牌结果, 局号:", request.getNoActive());
                return ResponseUtil.custom("请求错误");
        	}
        	betResultService.updateReopenByNoActive(request.getNoActive());
        	betResultChange.setAwardOption(betResult.getAwardOption());
        }
        
        betResultChangeService.save(betResultChange);
        return ResponseUtil.success();
    }

    @PostMapping("page")
    @ApiOperation("订单查询")
    public ResponseEntity<Page<BetResultChange>> page(BetResultChangePageVO vo) {
        log.info("订单查询");
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BetResultChange> pageList = betResultChangeService.getBetResultChangePage(vo, pageable);
        return ResponseUtil.success(pageList);
    }
}
