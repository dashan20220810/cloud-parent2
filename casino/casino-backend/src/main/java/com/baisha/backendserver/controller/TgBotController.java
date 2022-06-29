package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.business.DeskService;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.desk.DeskListBO;
import com.baisha.backendserver.model.bo.tgBot.TgBotPageBO;
import com.baisha.backendserver.model.bo.tgBot.TgGroupPageBO;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.StatusVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotGroupAuditVO;
import com.baisha.backendserver.model.vo.tgBot.TgBotPageVO;
import com.baisha.backendserver.model.vo.tgBot.TgGroupPageVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.backendserver.util.constants.TgBotServerConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author kimi
 */
@Slf4j
@Api(tags = "机器人管理")
@RestController
@RequestMapping("tgBot")
public class TgBotController {

    @Value("${url.tgBotServer}")
    private String tgBotServerUrl;
    @Autowired
    private CommonService commonService;
    @Autowired
    private DeskService deskService;

    @ApiOperation("新开机器人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "机器人名称", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "token", value = "机器人token", required = true, dataTypeClass = String.class)
    })
    @PostMapping("open")
    public ResponseEntity open(String username, String token) {
        // 参数校验
        if (CommonUtil.checkNull(username, token)) {
            return ResponseUtil.parameterNotNull();
        }
        // 后台登陆用户
        Admin current = commonService.getCurrentUser();
        String url = tgBotServerUrl + TgBotServerConstants.OPEN_TG_BOT;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", username);
        paramMap.put("token", token);
        String result = HttpClient4Util.doPost(url, paramMap);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        log.info("{} {} {} {}", current.getUserName(), BackendConstants.INSERT, JSON.toJSONString(paramMap), BackendConstants.TOBOT_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation("机器人分页查询")
    @GetMapping("page")
    public ResponseEntity<Page<TgBotPageBO>> page(TgBotPageVO vo) {
        String url = tgBotServerUrl + TgBotServerConstants.PAGE_TG_BOT;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation("更新状态")
    @PostMapping("updateStatus")
    public ResponseEntity updateStatus(StatusVO statusVO) {
        Long id = statusVO.getId();
        Integer status = statusVO.getStatus();
        // 参数校验
        if (CommonUtil.checkNull(id.toString(), status.toString())) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.UPDATE_STATUS_TG_BOT;
        Map<String, Object> param = BackendServerUtil.objectToMap(statusVO);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin current = commonService.getCurrentUser();
        log.info("{} {} {} {}", current.getUserName(), BackendConstants.UPDATE, JSON.toJSONString(statusVO), BackendConstants.TOBOT_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation("机器人删除")
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (null == vo.getId() || vo.getId().intValue() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.DELETE_TG_BOT;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin current = commonService.getCurrentUser();
        log.info("{} {} {} {}", current.getUserName(), BackendConstants.DELETE, "删除机器人" + JSON.toJSONString(vo)
                , BackendConstants.TOBOT_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }


    @GetMapping("group/page")
    @ApiOperation(("获取机器人下的电报群分页"))
    public ResponseEntity<Page<TgGroupPageBO>> groupPage(TgGroupPageVO vo) {
        if (null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.GET_GROUP;
        StringBuffer sb = new StringBuffer();
        sb.append(url + "?pageNumber=" + vo.getPageNumber() +
                "&pageSize=" + vo.getPageSize() + "&botId=" + vo.getId());

        String result = HttpClient4Util.doGet(sb.toString());
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }

        ResponseEntity responseEntity = JSON.parseObject(result, ResponseEntity.class);
        JSONObject page = (JSONObject) responseEntity.getData();
        if (Objects.nonNull(page)) {
            List<TgGroupPageBO> list = JSONArray.parseArray(page.getString("content"), TgGroupPageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                List<DeskListBO> deskList = deskService.findAllDeskList();
                for (TgGroupPageBO bo : list) {
                    setTableCode(bo, deskList);
                }
                page.put("content", list);
                responseEntity.setData(page);
            }
        }
        return responseEntity;
    }

    private void setTableCode(TgGroupPageBO bo, List<DeskListBO> deskList) {
        if (!CollectionUtils.isEmpty(deskList) && null != bo.getTableId()) {
            for (DeskListBO deskListBO : deskList) {
                if (bo.getTableId().equals(deskListBO.getTableId())) {
                    bo.setDeskCode(deskListBO.getDeskCode());
                    bo.setName(deskListBO.getName());
                }
            }
        }
    }

    @ApiOperation(("机器人与TG群关系审核"))
    @PostMapping("group/audit")
    public ResponseEntity groupAudit(TgBotGroupAuditVO vo) {
        if (null == vo.getId() || null == vo.getStatus()) {
            return ResponseUtil.parameterNotNull();
        }
        //数据处理
        vo = setTgBotGroupAuditVO(vo);
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if ((vo.getMinAmount().compareTo(vo.getMaxAmount()) > 0)
                || (vo.getMinAmount().compareTo(vo.getMaxShoeAmount()) > 0)
                || (vo.getMaxAmount().compareTo(vo.getMaxShoeAmount()) > 0)) {
            return new ResponseEntity<>("限红值不规范");
        }

        String url = tgBotServerUrl + TgBotServerConstants.GROUP_AUDIT;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        param.put("state", vo.getStatus());
        param.put("chatId", vo.getId());
        param.remove("status");
        param.remove("id");
        System.out.println(JSON.toJSONString(param));
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        log.info("{} {} 审核群(限红){} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                JSON.toJSONString(param), BackendConstants.TOBOT_GROUP_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }

    private TgBotGroupAuditVO setTgBotGroupAuditVO(TgBotGroupAuditVO vo) {
        if (Constants.open.equals(vo.getStatus())) {
            //审核通过必传
            if (null == vo.getTableId() || vo.getTableId() < 0
                    || null == vo.getMinAmount() || vo.getMinAmount() < 0
                    || null == vo.getMaxAmount() || vo.getMaxAmount() < 0
                    || null == vo.getMaxShoeAmount() || vo.getMaxShoeAmount() < 0) {
                return null;
            }
        } else {
            //默认值
            if (null == vo.getTableId()) {
                //无值 就取一个，传给电报接口
                List<DeskListBO> deskList = deskService.findAllDeskList();
                if (!CollectionUtils.isEmpty(deskList)) {
                    vo.setTableId(deskList.get(0).getTableId());
                } else {
                    vo.setTableId(null);
                }
            }
            if (null == vo.getMinAmount()) {
                vo.setMinAmount(20);
            }
            if (null == vo.getMaxAmount()) {
                vo.setMaxAmount(15000);
            }
            if (null == vo.getMaxShoeAmount()) {
                vo.setMaxShoeAmount(50000);
            }
        }
        return vo;
    }

    @ApiOperation("机器人与TG群关系删除")
    @PostMapping(value = "group/delete")
    public ResponseEntity deleteGroup(IdVO vo) {
        if (null == vo.getId() || vo.getId() < 0) {
            return ResponseUtil.parameterNotNull();
        }
        String url = tgBotServerUrl + TgBotServerConstants.GROUP_DELETEBYID;
        Map<String, Object> param = new HashMap<>(16);
        param.put("chatId", vo.getId());
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.DELETE,
                JSON.toJSONString(param), BackendConstants.TOBOT_GROUP_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }


}
