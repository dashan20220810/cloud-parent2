package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.SysTelegramParameter;
import com.baisha.backendserver.model.bo.sys.SysTelegramParameterBO;
import com.baisha.backendserver.model.vo.sys.SysTelegramParameterVO;
import com.baisha.backendserver.service.SysTelegramService;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping(value = "sys/telegram")
@Api(tags = "系统设置-电报")
public class SysTelegramController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CommonService commonService;
    @Autowired
    private SysTelegramService sysTelegramService;
    @Autowired
    private TelegramService telegramService;

    @ApiOperation("获取信息")
    @GetMapping(value = "getInfo")
    public ResponseEntity<SysTelegramParameterBO> getSysTelegramInfo() {
        SysTelegramParameter sysTelegram = sysTelegramService.getSysTelegram();
        SysTelegramParameterBO bo = new SysTelegramParameterBO();
        BeanUtils.copyProperties(sysTelegram, bo);
        if (StringUtils.isNotEmpty(bo.getStartBetPicUrl())) {
            bo.setStartBetPicUrlShow(commonService.getFileServerUrl(bo.getStartBetPicUrl()));
        }
        System.out.println(JSONObject.toJSONString(telegramService.getTelegramSet()));
        return ResponseUtil.success(bo);
    }

    @ApiOperation("设置信息")
    @GetMapping(value = "setInfo")
    public ResponseEntity<Long> setSysTelegramInfo(SysTelegramParameterVO vo) {
        //不能全部传空
        if (null == vo.getId()
                && StringUtils.isEmpty(vo.getStartBetPicUrl())
                && StringUtils.isEmpty(vo.getOnlyFinance())
                && StringUtils.isEmpty(vo.getOnlyCustomerService())) {
            return new ResponseEntity("至少传一个参数");
        }
        Admin admin = commonService.getCurrentUser();
        SysTelegramParameter stp;
        if (null == vo.getId()) {
            //新增
            int size = sysTelegramService.findAllSize();
            if (size > 0) {
                return new ResponseEntity("已存在数据,请传ID参数");
            }
            stp = new SysTelegramParameter();
            BeanUtils.copyProperties(vo, stp);
            stp.setCreateBy(admin.getUserName());
        } else {
            //编辑
            //有ID则查询是否存在
            stp = sysTelegramService.findById(vo.getId());
            if (Objects.isNull(stp)) {
                return new ResponseEntity("对应ID无数据");
            }
            if (StringUtils.isNotEmpty(vo.getOnlyFinance())) {
                stp.setOnlyFinance(vo.getOnlyFinance());
            }
            if (StringUtils.isNotEmpty(vo.getOnlyCustomerService())) {
                stp.setOnlyCustomerService(vo.getOnlyCustomerService());
            }
            if (StringUtils.isNotEmpty(vo.getStartBetPicUrl())) {
                stp.setStartBetPicUrl(vo.getStartBetPicUrl());
            }
        }
        stp.setUpdateBy(admin.getUpdateBy());
        sysTelegramService.save(stp);
        doSetRedis(stp);
        log.info("{} {} {} {}", admin.getUserName(), BackendConstants.UPDATE, JSON.toJSONString(admin), BackendConstants.SYS_TELEGRAM_MODULE);
        return ResponseUtil.success(stp.getId());
    }

    private void doSetRedis(SysTelegramParameter stp) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("onlyFinance", StringUtils.isEmpty(stp.getOnlyFinance()) ? "" : stp.getOnlyFinance());
        map.put("onlyCustomerService", StringUtils.isEmpty(stp.getOnlyCustomerService()) ? "" :
                stp.getOnlyCustomerService());
        map.put("startBetPicUrl", StringUtils.isEmpty(stp.getStartBetPicUrl()) ? "" :
                commonService.getFileServerUrl(stp.getStartBetPicUrl()));
        redisUtil.hmset(RedisKeyConstants.SYS_TELEGRAM, map);
    }


}
