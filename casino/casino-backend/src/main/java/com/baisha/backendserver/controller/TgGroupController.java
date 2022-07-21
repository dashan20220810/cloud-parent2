package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.bo.tgGroup.TgGroupManagePageBO;
import com.baisha.backendserver.model.vo.PageVO;
import com.baisha.backendserver.model.vo.tgGroup.TgGroupBindVO;
import com.baisha.backendserver.util.constants.TgBotServerConstants;
import com.baisha.modulecommon.reponse.ResponseCode;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author yihui
 */
@Slf4j
@Api(tags = "TG群管理")
@RestController
@RequestMapping(value = "tgGroup")
public class TgGroupController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;
    @Autowired
    private CommonBusiness commonService;

    @ApiOperation(value = "TG群分页")
    @GetMapping(value = "getTgGroupPage")
    public ResponseEntity<Page<TgGroupManagePageBO>> getTgGroupPage(PageVO vo) {
        String url = tgBotServerUrl + TgBotServerConstants.GET_GROUP;
        StringBuffer sb = new StringBuffer();
        sb.append(url + "?pageNumber=" + vo.getPageNumber() + "&pageSize=" + vo.getPageSize());

        String result = HttpClient4Util.doGet(sb.toString());
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }

        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        JSONObject page = (JSONObject) responseEntity.getData();
        if (Objects.nonNull(page) && responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            List<TgGroupManagePageBO> list = JSONArray.parseArray(page.getString("content"), TgGroupManagePageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                page.put("content", list);
                responseEntity.setData(page);
            }
        }
        return responseEntity;
    }

    @ApiOperation(value = "TG群下的投注机器人")
    @GetMapping(value = "getBetBotByTgGroup")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataTypeClass = String.class)})
    public ResponseEntity getBetBotByTgGroup(Long id) {
        if (null == id) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_FINDRELATIONBYTGCHATID;
        StringBuffer sb = new StringBuffer();
        sb.append(url + "?tgChatId=" + id);
        String result = HttpClient4Util.doGet(sb.toString());
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }

        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }


    @ApiOperation(value = "绑定机器人与群关系")
    @PostMapping(value = "bindTgGroup")
    public ResponseEntity bindTgGroup(TgGroupBindVO vo) {
        if (StringUtils.isEmpty(vo.getTgBetBotIds()) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }

        String[] arr = vo.getTgBetBotIds().split(",");
        if (null == arr || arr.length == 0) {
            return new ResponseEntity("请选择机器人");
        }
        List<Long> tgBetBotIdsList = new ArrayList<>();
        for (String tgBetBotId : arr) {
            if (StringUtils.isNotEmpty(tgBetBotId)) {
                tgBetBotId = tgBetBotId.trim();
                tgBetBotIdsList.add(Long.parseLong(tgBetBotId));
            }
        }

        if (CollectionUtils.isEmpty(tgBetBotIdsList)) {
            return new ResponseEntity("没有选择机器人");
        }
        String url = tgBotServerUrl + TgBotServerConstants.TGBETBOT_CONFIRMBIND;
        Map<String, Object> param = new HashMap<>(16);
        param.put("tgChatId", vo.getId());
        param.put("tgBetBotIds", tgBetBotIdsList);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            log.error("绑定机器人与群关系");
            return ResponseUtil.fail();
        }

        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        return responseEntity;
    }


}
