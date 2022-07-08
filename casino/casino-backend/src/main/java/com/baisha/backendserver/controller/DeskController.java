package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.business.DeskBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.desk.DeskListBO;
import com.baisha.backendserver.model.bo.desk.DeskPageBO;
import com.baisha.backendserver.model.bo.desk.GameBaccOddsBO;
import com.baisha.backendserver.model.bo.desk.GameOddsListBO;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.StatusVO;
import com.baisha.backendserver.model.vo.desk.DeskAddVO;
import com.baisha.backendserver.model.vo.desk.DeskPageVO;
import com.baisha.backendserver.model.vo.desk.DeskUpdateVO;
import com.baisha.backendserver.model.vo.desk.GameBaccOddsVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.modulecommon.enums.TgBaccRuleEnum;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping(value = "desk")
@Api(tags = "桌台管理/游戏赔率")
public class DeskController {

    @Autowired
    private CommonBusiness commonService;
    @Autowired
    private DeskBusiness deskService;
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

    @ApiOperation("获取桌台分页列表")
    @GetMapping(value = "page")
    public ResponseEntity<Page<DeskPageBO>> page(DeskPageVO vo) {
        String url = gameServerUrl + GameServerConstants.DESK_PAGE;
        StringBuffer sb = new StringBuffer();
        sb.append(url);
        sb.append("?pageNumber=" + vo.getPageNumber() + "&pageSize=" + vo.getPageSize());
        if (StringUtils.isNotEmpty(vo.getDeskCode())) {
            sb.append("&deskCode=" + vo.getDeskCode());
        }
        if (StringUtils.isNotEmpty(vo.getGameCode())) {
            sb.append("&gameCode=" + vo.getGameCode());
        }
        if (StringUtils.isNotEmpty(vo.getLocalIp())) {
            sb.append("&localIp=" + vo.getLocalIp());
        }
        if (null != vo.getStatus()) {
            sb.append("&status=" + vo.getStatus());
        }

        String result = HttpClient4Util.doGet(sb.toString());
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation("桌台删除")
    @PostMapping(value = "delete")
    public ResponseEntity delete(IdVO vo) {
        if (null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        String url = gameServerUrl + GameServerConstants.DESK_DELETE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("deskId", vo.getId());
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.DELETE,
                    JSON.toJSONString(param), BackendConstants.DESK_MODULE);
        }
        return responseEntity;
    }

    @ApiOperation("桌台状态更新")
    @PostMapping(value = "status")
    public ResponseEntity status(StatusVO vo) {
        if (null == vo.getId() || vo.getId() < 0 || null == vo.getStatus()) {
            return ResponseUtil.parameterNotNull();
        }
        if (BackendServerUtil.checkStatus(vo.getStatus())) {
            return new ResponseEntity("状态不规范");
        }
        String url = gameServerUrl + GameServerConstants.DESK_UPDATESTATUS;
        Map<String, Object> param = new HashMap<>(16);
        param.put("deskId", vo.getId());
        param.put("status", vo.getStatus());
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    JSON.toJSONString(param), BackendConstants.DESK_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation("桌台新增")
    @PostMapping(value = "add")
    public ResponseEntity add(DeskAddVO vo) {
        if (CommonUtil.checkNull(vo.getDeskCode(), vo.getGameCode(), vo.getLocalIp()) || null == vo.getStatus()) {
            return ResponseUtil.parameterNotNull();
        }
        String url = gameServerUrl + GameServerConstants.DESK_ADD;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.INSERT,
                    JSON.toJSONString(param), BackendConstants.DESK_MODULE);
        }
        return responseEntity;
    }

    @ApiOperation("桌台编辑")
    @PostMapping(value = "update")
    public ResponseEntity update(DeskUpdateVO vo) {
        if (CommonUtil.checkNull(vo.getGameCode(), vo.getLocalIp())
                || null == vo.getStatus()
                || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        String url = gameServerUrl + GameServerConstants.DESK_UPDATE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        param.put("deskId", vo.getId());
        param.remove("id");
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    JSON.toJSONString(param), BackendConstants.DESK_MODULE);
        }
        return responseEntity;
    }


    @ApiOperation("获取游戏编码列表")
    @GetMapping(value = "gameCode/list")
    public ResponseEntity gameCodeList() {
        String url = gameServerUrl + GameServerConstants.GAME_CODE_LIST;
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }


    @ApiOperation("获取游戏赔率")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gameCode", value = "游戏编码", required = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "getGameOdds")
    public ResponseEntity<GameBaccOddsBO> getGameOdds(String gameCode) {
        if (StringUtils.isEmpty(gameCode)) {
            return ResponseUtil.parameterNotNull();
        }
        String url = gameServerUrl + GameServerConstants.GAME_ODDS_LIST + "?gameCode=" + gameCode.toUpperCase();
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }

        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            JSONArray jsonArray = (JSONArray) responseEntity.getData();
            if (CollectionUtils.isEmpty(jsonArray)) {
                //默认
                return ResponseUtil.success(new GameBaccOddsBO());
            } else {
                List<GameOddsListBO> list = JSONArray.parseArray(JSONObject.toJSONString(jsonArray), GameOddsListBO.class);
                //转换
                GameBaccOddsBO bo = transOddsBo(list);
                return ResponseUtil.success(bo);
            }
        }

        return ResponseUtil.fail();
    }

    private GameBaccOddsBO transOddsBo(List<GameOddsListBO> list) {
        GameBaccOddsBO bo = new GameBaccOddsBO();
        for (GameOddsListBO g : list) {
            BigDecimal odds = g.getOdds();
            String ruleCode = g.getRuleCode().toUpperCase();
            if (ruleCode.equals(TgBaccRuleEnum.Z.getCode())) {
                bo.setZ(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.X.getCode())) {
                bo.setX(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.H.getCode())) {
                bo.setH(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.ZD.getCode())) {
                bo.setZd(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.XD.getCode())) {
                bo.setXd(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.SS2.getCode())) {
                bo.setSs2(odds);
            }
            if (ruleCode.equals(TgBaccRuleEnum.SS3.getCode())) {
                bo.setSs3(odds);
            }
        }

        return bo;
    }

    @ApiOperation("设置TG百家乐游戏赔率")
    @PostMapping(value = "setBaccOdds")
    public ResponseEntity doSetBaccOdds(GameBaccOddsVO vo) {
        if (StringUtils.isEmpty(vo.getGameCode())) {
            return ResponseUtil.parameterNotNull();
        }
        if (null == vo.getX() || vo.getX().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getZ() || vo.getZ().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getH() || vo.getH().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getZd() || vo.getZd().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getXd() || vo.getXd().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getSs2() || vo.getSs2().compareTo(BigDecimal.ZERO) <= 0
                || null == vo.getSs3() || vo.getSs3().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity("赔率不规范");
        }

        String url = gameServerUrl + GameServerConstants.GAME_SET_BACC_ODDS;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            Admin currentUser = commonService.getCurrentUser();
            log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                    JSON.toJSONString(param), BackendConstants.BACC_ODDS_MODULE);
        }
        return responseEntity;

    }


}
