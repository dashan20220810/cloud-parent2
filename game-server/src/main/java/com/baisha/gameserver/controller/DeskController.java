package com.baisha.gameserver.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.gameserver.model.Desk;
import com.baisha.gameserver.service.DeskService;
import com.baisha.gameserver.vo.DeskPageVO;
import com.baisha.gameserver.vo.DeskUpdateVO;
import com.baisha.gameserver.vo.DeskVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("desk")
@Api(tags = {"桌台控制器"})
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

    @GetMapping("page")
    @ApiOperation("桌台查询")
    public ResponseEntity<Page<Desk>> page(DeskPageVO vo) {

        log.info("桌台查询");
        Page<Desk> pageList = deskService.getDeskPage(vo);
        return ResponseUtil.success(pageList);
    }

    @GetMapping("queryByLocalIp")
    @ApiOperation("桌台查询-LOCAL IP")
    public ResponseEntity<Desk> queryByLocalIp(String localIp) {

        log.info("桌台查询-LOCAL IP");
        Desk result = deskService.findByLocalIp(localIp);
        if (result == null) {
            log.warn("桌台查询-LOCAL IP 失敗");
            return ResponseUtil.fail();
        }

        return ResponseUtil.success(result);
    }

    @GetMapping("queryByDeskCode")
    @ApiOperation("桌台查询-DeskCode")
    public ResponseEntity<Desk> queryByDeskCode(String deskCode) {

        log.info("桌台查询-DeskCode");
        Desk result = deskService.findByDeskCode(deskCode);
        if (result == null) {
            log.warn("桌台查询-DeskCode 失敗");
            return ResponseUtil.fail();
        }

        return ResponseUtil.success(result);
    }

    @GetMapping("queryById")
    @ApiOperation("桌台查询-id")
    public ResponseEntity<Desk> queryById(Long tableId) {

        log.info("桌台查询-id");
        Desk result = deskService.findById(tableId);
        if (result == null) {
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

        if (Desk.checkRequest(desk) == false) {
            log.info("[桌台新增] 检核失败");
            return ResponseUtil.fail();
        }

        try {
        	String validateDuplicate = deskService.validateDuplicateField(null, deskVO.getDeskCode(), deskVO.getName(), deskVO.getLocalIp());
        	if (StringUtils.isNotBlank(validateDuplicate)) {
        		return ResponseUtil.custom(validateDuplicate);
        	}
        	
        	deskService.save(desk);
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        	if (e.getCause() instanceof ConstraintViolationException) {
        		String eMsg = e.getCause().getCause().getMessage();
        		if ( eMsg.contains(deskVO.getLocalIp()) ) {
            		return ResponseUtil.custom("内网IP已占用");
        		} else if ( eMsg.contains(deskVO.getDeskCode()) ) {
                	return ResponseUtil.custom("桌台编号已存在或被占用");
        		} else if ( eMsg.contains(deskVO.getName()) ) {
                	return ResponseUtil.custom("桌台名称已存在或被占用");
        		} else {
            		return ResponseUtil.fail();
        		}
        	}
    		return ResponseUtil.fail();
        }

        log.info("[桌台新增] 成功!");
        return ResponseUtil.success();
    }

    @PostMapping("delete")
    @ApiOperation("桌台删除")
    public ResponseEntity<String> delete(Long deskId) {

        log.info("[桌台删除] ");

        if (deskId == null) {
            log.info("[桌台删除] 检核失败");
            return ResponseUtil.fail();
        }

        deskService.delete(deskId);

        log.info("[桌台删除] 成功!");
        return ResponseUtil.success();
    }

    @PostMapping("updateStatus")
    @ApiOperation("桌台状态更新")
    public ResponseEntity<String> updateStatus(Long deskId, Integer status) {

        log.info("[桌台状态更新] ");

        if (deskId == null || status == null) {
            log.info("[桌台状态更新] 检核失败");
            return ResponseUtil.fail();
        }

        deskService.updateStatus(deskId, status);

        log.info("[桌台状态更新] 成功!");
        return ResponseUtil.success();
    }

    @PostMapping("update")
    @ApiOperation("桌台更新")
    public ResponseEntity<String> update(Long deskId, DeskUpdateVO deskVO) {

        log.info("[桌台更新] ");
        Desk desk = deskVO.generateDesk();

        if (Desk.checkUpdateRequest(desk) == false) {
            log.info("[桌台更新] 检核失败");
            return ResponseUtil.fail();
        }

        try {
        	String validateDuplicate = deskService.validateDuplicateField(deskId, desk.getDeskCode(), deskVO.getName(), deskVO.getLocalIp());
        	if (StringUtils.isNotBlank(validateDuplicate)) {
        		return ResponseUtil.custom(validateDuplicate);
        	}
        	
            deskService.update(deskId, deskVO.getLocalIp(), deskVO.getVideoAddress(), deskVO.getNearVideoAddress()
                    , deskVO.getCloseVideoAddress(), deskVO.getGameCode(), deskVO.getStatus(), deskVO.getName());
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        	if (e.getCause() instanceof ConstraintViolationException) {
        		String eMsg = e.getCause().getCause().getMessage();
        		if ( eMsg.contains(deskVO.getLocalIp()) ) {
            		return ResponseUtil.custom("内网IP已占用");
        		} else if ( eMsg.contains(deskVO.getName()) ) {
                	return ResponseUtil.custom("桌台名称已存在或被占用");
        		} else {
            		return ResponseUtil.fail();
        		}
        	}
    		return ResponseUtil.fail();
        }

        log.info("[桌台更新] 成功!");
        return ResponseUtil.success();
    }

}
