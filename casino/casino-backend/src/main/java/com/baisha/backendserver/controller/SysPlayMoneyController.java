package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.SysPlayMoneyParameter;
import com.baisha.backendserver.model.bo.sys.SysPlayMoneyParameterBO;
import com.baisha.backendserver.model.vo.sys.SysPlayMoneyParameterVO;
import com.baisha.backendserver.service.SysPlayMoneyService;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "sys/playMoney")
@Api(tags = "系统设置-打码量倍率")
public class SysPlayMoneyController {

    @Autowired
    private CommonBusiness commonService;
    @Autowired
    private SysPlayMoneyService sysPlayMoneyService;

    @ApiOperation("获取信息")
    @GetMapping(value = "getInfo")
    public ResponseEntity<SysPlayMoneyParameterBO> getSysPlayMoneyInfo() {
        SysPlayMoneyParameter sysPlayMoney = sysPlayMoneyService.getSysPlayMoney();
        SysPlayMoneyParameterBO bo = new SysPlayMoneyParameterBO();
        BeanUtils.copyProperties(sysPlayMoney, bo);
        return ResponseUtil.success(bo);
    }

    @ApiOperation("设置信息")
    @PostMapping(value = "setInfo")
    public ResponseEntity<Long> setSysPlayMoneyInfo(SysPlayMoneyParameterVO vo) {
        if (null == vo.getRecharge() || vo.getRecharge().doubleValue() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        Admin admin = commonService.getCurrentUser();
        SysPlayMoneyParameter stp;
        if (null == vo.getId()) {
            //新增
            int size = sysPlayMoneyService.findAllSize();
            if (size > 0) {
                return new ResponseEntity("已存在数据,请传ID参数");
            }
            stp = new SysPlayMoneyParameter();
            BeanUtils.copyProperties(vo, stp);
            stp.setCreateBy(admin.getUserName());
        } else {
            //有ID则查询是否存在
            stp = sysPlayMoneyService.findById(vo.getId());
            if (Objects.isNull(stp)) {
                return new ResponseEntity("对应ID无数据");
            }
            stp.setRecharge(vo.getRecharge());
        }
        stp.setUpdateBy(admin.getUpdateBy());
        sysPlayMoneyService.save(stp);
        log.info("{} {} {} {}", admin.getUserName(), BackendConstants.UPDATE, JSON.toJSONString(stp), BackendConstants.SYS_PLAYMONEY_MODULE);
        return ResponseUtil.success(stp.getId());
    }


}
