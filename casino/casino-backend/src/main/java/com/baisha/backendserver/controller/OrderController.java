package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.model.bo.order.BetPageBO;
import com.baisha.backendserver.model.vo.order.BetPageVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.enums.BetStatusEnum;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: alvin
 */
@RestController
@Api(tags = "订单管理")
@RequestMapping("order")
@Slf4j
public class OrderController {

    @Value("${url.gameServer}")
    private String gameServerUrl;

    @PostMapping("page")
    @ApiOperation(("订单查询"))
    public ResponseEntity<Page<BetPageBO>> page(BetPageVO betRequest) {
        Map<String, Object> params = BackendServerUtil.objectToMap(betRequest);
        String result = HttpClient4Util.doPost(gameServerUrl + GameServerConstants.ORDER_PAGE, params);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (Objects.nonNull(responseEntity) && responseEntity.getCode() == 0) {
            JSONObject page = (JSONObject) responseEntity.getData();
            List<BetPageBO> list = JSONArray.parseArray(page.getString("content"), BetPageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                for (BetPageBO bo : list) {
                    setBetBo(bo);
                }
                page.put("content", list);
                responseEntity.setData(page);
            }
        }
        return responseEntity;
    }

    private void setBetBo(BetPageBO bo) {
        getBetStatusName(bo);
        getBetTotalAmount(bo);
    }

    private void getBetTotalAmount(BetPageBO bo) {
        StringBuffer sb = new StringBuffer();
        sb.append("总金额:");
        sb.append(bo.getAmountZ() + bo.getAmountX() + bo.getAmountH() + bo.getAmountZd() + bo.getAmountXd() + bo.getAmountSs());
        sb.append("(");
        if (bo.getAmountZ() > 0) {
            sb.append("庄-" + bo.getAmountZ());
        }
        if (bo.getAmountX() > 0) {
            sb.append("闲-" + bo.getAmountX());
        }
        if (bo.getAmountH() > 0) {
            sb.append("和-" + bo.getAmountH());
        }
        if (bo.getAmountZd() > 0) {
            sb.append("庄对-" + bo.getAmountZd());
        }
        if (bo.getAmountXd() > 0) {
            sb.append("闲对-" + bo.getAmountXd());
        }
        if (bo.getAmountSs() > 0) {
            sb.append("幸运6-" + bo.getAmountSs());
        }
        sb.append(")");
        bo.setTotalAmount(sb.toString());
    }

    private void getBetStatusName(BetPageBO bo) {
        if (bo.getStatus().equals(BetStatusEnum.BET.getCode())) {
            bo.setStatusName("下注");
            return;
        }
        if (bo.getStatus().equals(BetStatusEnum.SETTLEMENT.getCode())) {
            bo.setStatusName("结算");
            return;
        }
    }


    //@GetMapping("betOption")
    //@ApiOperation("下注类型")
    public ResponseEntity<List<Map<String, String>>> betOption() {
        return ResponseUtil.success(BetOption.getList()
                .stream()
                .map(option -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", option.getDisplay());
                    map.put("value", option.toString());
                    return map;
                }).collect(Collectors.toList()));
    }

    public List<Map<String, String>> getBetOption() {
        return BetOption.getList()
                .stream()
                .map(option -> {
                    Map<String, String> map = new HashMap<>(16);
                    map.put("name", option.getDisplay());
                    map.put("value", option.toString());
                    return map;
                }).collect(Collectors.toList());
    }

    public String getBetOptionName(List<Map<String, String>> options, String betOption) {
        for (Map<String, String> m : options) {
            if (m.get("value").equals(betOption)) {
                return m.get("name");
            }
        }
        return null;
    }
}
