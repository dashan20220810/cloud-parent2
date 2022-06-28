package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.business.DeskService;
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
    private DeskService deskService;
    @Value("${url.gameServer}")
    private String gameServerUrl;

    @ApiOperation("获取全部桌台列表")
    @GetMapping(value = "findAllDeskList")
    public ResponseEntity<List<DeskListBO>> findAllDeskList() {
        List<DeskListBO> deskList = deskService.findAllDeskList();
        if (Objects.isNull(deskList)) {
            return ResponseUtil.fail();
        }
        return ResponseUtil.success(deskList);
    }




}
