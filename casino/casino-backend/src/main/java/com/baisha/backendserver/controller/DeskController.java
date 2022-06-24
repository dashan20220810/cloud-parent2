package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.SysTelegramParameter;
import com.baisha.backendserver.model.TgGroupBound;
import com.baisha.backendserver.model.vo.sys.SysTelegramParameterVO;
import com.baisha.backendserver.model.vo.tgBot.TgGroupBoundVO;
import com.baisha.backendserver.service.TgGroupBoundService;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
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
@RequestMapping(value = "desk")
@Api(tags = "限红")
public class DeskController {

    @Autowired
    private CommonService commonService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TgGroupBoundService tgGroupBoundService;

    @ApiOperation("设置鲜红")
    @GetMapping(value = "setInfo")
    public ResponseEntity<Long> setTgGroupBoundInfo(TgGroupBoundVO vo) {
        if (StringUtils.isEmpty(vo.getTgGroupId())
                || Objects.isNull(vo.getMaxAmount())
                || Objects.isNull(vo.getMinAmount())
                || Objects.isNull(vo.getMaxShoeAmount())) {
            return ResponseUtil.parameterNotNull();
        }
        Integer ZERO = 0;
        if (vo.getMinAmount().compareTo(ZERO) <= 0
                || vo.getMaxAmount().compareTo(ZERO) <= 0
                || vo.getMaxShoeAmount().compareTo(ZERO) <= 0) {
            return new ResponseEntity("必须大于0的整数");
        }

        Admin admin = commonService.getCurrentUser();
        TgGroupBound tgg;
        synchronized (vo.getTgGroupId()) {
            tgg = tgGroupBoundService.findByTgGroupId(vo.getTgGroupId());
            if (Objects.isNull(tgg)) {
                tgg = new TgGroupBound();
                //新增
                BeanUtils.copyProperties(vo, tgg);
                tgg.setCreateBy(admin.getUserName());
                tgg.setUpdateBy(admin.getUserName());
            } else {
                //编辑
                tgg.setMaxAmount(vo.getMaxAmount());
                tgg.setMinAmount(vo.getMinAmount());
                tgg.setMaxShoeAmount(vo.getMaxShoeAmount());
            }
            tgGroupBoundService.save(tgg);
            doSetRedis(tgg);
            log.info("{} {} {} {}", admin.getUserName(), BackendConstants.UPDATE, JSON.toJSONString(tgg), BackendConstants.TELEGRAM_BOUND_MODULE);
            return ResponseUtil.success(tgg.getId());
        }
    }

    private void doSetRedis(TgGroupBound tgg) {
        Map<String, Object> map = new HashMap<>();
        map.put("tgGroupId", tgg.getTgGroupId());
        map.put("minAmount", tgg.getMinAmount());
        map.put("maxAmount", tgg.getMaxAmount());
        map.put("maxShoeAmount", tgg.getMaxShoeAmount());
        redisUtil.hset(RedisKeyConstants.GROUP_TELEGRAM_BOUND, tgg.getTgGroupId(), map);
    }


}
