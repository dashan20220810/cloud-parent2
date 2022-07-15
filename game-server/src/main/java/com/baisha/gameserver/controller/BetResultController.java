package com.baisha.gameserver.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.BetResult;
import com.baisha.gameserver.service.BetResultService;
import com.baisha.gameserver.vo.BetResultPageVO;
import com.baisha.gameserver.vo.BetResultVO;
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
@Api(tags = "开牌结果管理")
@RequestMapping("betResult")
@Slf4j
public class BetResultController {

    @Autowired
    BetResultService betResultService;

    @PostMapping("add")
    @ApiOperation("开牌结果新增")
    public ResponseEntity<String> add(BetResultVO request) {

        log.info("[开牌结果] ");
        BetResult betResult = request.generateBetResult();

        if (betResult.checkRequest() == false) {
            log.info("[开牌结果] 检核失败");
            return ResponseUtil.custom("请求错误");
        }

        betResultService.save(betResult);

//		log.info("[下注] 成功! 押{} 共{}", betVO.getBetOption().getDisplay(), betVO.getAmount());
        return ResponseUtil.success();
    }

    @PostMapping("update")
    @ApiOperation("开牌结果-更新")
    public ResponseEntity<String> update(BetResultVO request) {

        log.info("[开牌结果-更新] ");
        BetResult betResult = request.generateBetResult();

        if ( StringUtils.isBlank(request.getNoActive())
        		|| StringUtils.isBlank(request.getAwardOption()) ) {
            log.info("[开牌结果-更新] 检核失败");
            return ResponseUtil.custom("请求错误");
        }

        betResultService.update( betResult.getNoActive(), betResult.getAwardOption() );
        return ResponseUtil.success();
    }

    @PostMapping("page")
    @ApiOperation("开牌结果分页")
    public ResponseEntity<Page<BetResult>> page(BetResultPageVO vo) {
        log.info("开牌结果分页");
        Pageable pageable = PageUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<BetResult> pageList = betResultService.getBetResultPage(vo, pageable);
        return ResponseUtil.success(pageList);
    }

    @GetMapping("queryByNoActive")
    @ApiOperation("开牌结果查询")
    public ResponseEntity<BetResult> queryByNoActive(String noActive) {

        log.info("开牌结果查询");
        BetResult result = betResultService.findByNoActive(noActive);
        if (result == null) {
            log.warn("开牌结果查询 失敗");
            return ResponseUtil.fail();
        }

        return ResponseUtil.success(result);
    }
}
