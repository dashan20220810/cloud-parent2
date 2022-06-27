package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.model.bo.desk.DeskListBO;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping(value = "desk")
@Api(tags = "桌台")
public class DeskController {

    @Autowired
    private CommonService commonService;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${url.gameServer}")
    private String gameServerUrl;

    @ApiOperation("获取全部桌台列表")
    @GetMapping(value = "findAllDeskList")
    public ResponseEntity<List<DeskListBO>> findAllDeskList() {
        String url = gameServerUrl + GameServerConstants.DESK_LIST;
        String result = HttpClient4Util.doGet(url);
        if (StringUtils.isEmpty(result)) {
            return ResponseUtil.fail();
        }
        List<DeskListBO> deskList = new ArrayList<>();
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == 0) {
            JSONArray data = (JSONArray) responseEntity.getData();
            if (Objects.nonNull(data)) {
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    JSONObject o = data.getJSONObject(i);
                    DeskListBO bo = new DeskListBO();
                    bo.setTableId(Long.parseLong(String.valueOf(o.get("id"))));
                    bo.setDeskCode(String.valueOf(o.get("deskCode")));
                    deskList.add(bo);
                }
            }
        }
        responseEntity.setData(deskList);
        return responseEntity;
    }


    //暂时去掉 做到telegram-server
    //@ApiOperation("设置限红")
    //@PostMapping(value = "setInfo")
    /*public ResponseEntity<Long> setTgGroupBoundInfo(TgGroupBoundVO vo) {
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
    }*/


}
