package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.business.DeskBusiness;
import com.baisha.backendserver.business.OpenAwardBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.BetResultChange;
import com.baisha.backendserver.model.bo.CodeNameBO;
import com.baisha.backendserver.model.bo.award.BetResultBO;
import com.baisha.backendserver.model.bo.award.BetResultPageBO;
import com.baisha.backendserver.model.bo.desk.DeskListBO;
import com.baisha.backendserver.model.vo.award.BetResultPageVO;
import com.baisha.backendserver.model.vo.award.BetResultRepairVO;
import com.baisha.backendserver.service.BetResultChangeService;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@Api(tags = "开奖管理")
@RestController
@RequestMapping(value = "betResult")
public class BetAwardController {
    @Value("${url.gameServer}")
    private String gameServerUrl;
    @Autowired
    private CommonBusiness commonService;
    @Autowired
    private DeskBusiness deskService;
    @Autowired
    private OpenAwardBusiness openAwardBusiness;
    @Autowired
    private BetResultChangeService betResultChangeService;


    @ApiOperation("获取开奖结果分页列表")
    @GetMapping(value = "page")
    public ResponseEntity<Page<BetResultPageBO>> page(BetResultPageVO vo) {
        String url = gameServerUrl + GameServerConstants.GAME_BET_RESULT_PAGE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        ResponseEntity betResultResponse = JSONObject.parseObject(result, ResponseEntity.class);
        if (betResultResponse.getCode() == ResponseCode.SUCCESS.getCode()) {
            JSONObject page = (JSONObject) betResultResponse.getData();
            List<BetResultPageBO> list = JSONArray.parseArray(page.getString("content"), BetResultPageBO.class);
            if (!CollectionUtils.isEmpty(list)) {
                List<DeskListBO> deskList = deskService.findAllDeskList();
                for (BetResultPageBO bo : list) {
                    setBetBo(bo, deskList);
                }
                page.put("content", list);
                betResultResponse.setData(page);
            }

        }
        return betResultResponse;
    }

    private void setBetBo(BetResultPageBO bo, List<DeskListBO> deskList) {
        String awardOption = bo.getAwardOption();
        if (StringUtils.isNotEmpty(awardOption)) {
            String[] awardOptionArr = awardOption.split(",");
            String name = "";
            for (String option : awardOptionArr) {
                name = TgBaccRuleEnum.valueOf(option).getName() + ",";
            }
            if (StringUtils.isNotEmpty(name)) {
                name = name.substring(0, name.length() - 1);
            }
            bo.setAwardOptionName(name);
        } else {
            bo.setAwardOptionName("未开");
        }

        for (DeskListBO desk : deskList) {
            if (bo.getTableId().equals(desk.getTableId())) {
                bo.setTableName(desk.getName());
            }
        }
    }


    @ApiOperation(value = "获取开奖选项")
    @GetMapping(value = "")
    public ResponseEntity getAwardOption() {
        return ResponseUtil.success(TgBaccRuleEnum.getList().stream()
                .map(item -> CodeNameBO.builder().code(item.getCode()).name(item.getName()).build()).toList());
    }

    @ApiOperation(value = "补单(不影响已结算的该局的会员)", notes = "未开奖 或 开奖后部分注单没结算 ")
    @PostMapping(value = "repair")
    public ResponseEntity repairBetResult(BetResultRepairVO vo) {
        if (CommonUtil.checkNull(vo.getNoActive(), vo.getAwardOption())) {
            return ResponseUtil.parameterNotNull();
        }
        log.info("补单--(web)传入参数 ：{}", JSONObject.toJSONString(vo));
        //先查询开奖结果是否 与原来一样
        BetResultBO resultBO = getBetResultBO(vo.getNoActive());
        if (Objects.isNull(resultBO)) {
            return null;
        }
        //检查传入的开奖结果是否规范
        BetResultRepairVO repairVO = doCheckAwardOption(resultBO, vo.getAwardOption(), vo.getNoActive());
        if (Objects.isNull(repairVO)) {
            return new ResponseEntity("未查询到局或开奖选项不正确(已开的不能修改选项)");
        }
        log.info("补单--参数 ：{}", JSONObject.toJSONString(repairVO));

        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        openAwardBusiness.repairBetResult(repairVO);
        //日志记录
        doSaveChang(currentUser, repairVO, resultBO);
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                JSON.toJSONString(vo), BackendConstants.BET_RESULT_MODULE);
        return ResponseUtil.success();
    }

    private void doSaveChang(Admin currentUser, BetResultRepairVO repairVO, BetResultBO resultBO) {
        BetResultChange change = new BetResultChange();
        change.setTableId(resultBO.getTableId());
        change.setNoActive(resultBO.getNoActive());
        change.setAwardOption(resultBO.getAwardOption());
        change.setFinalAwardOption(repairVO.getAwardOption());
        change.setCreateBy(currentUser.getUserName());
        change.setUpdateBy(currentUser.getUserName());
        betResultChangeService.save(change);
    }

    private BetResultRepairVO doCheckAwardOption(BetResultBO resultBO, String awardOption, String noActive) {
        //先查询开奖结果是否 与原来一样
        BetResultRepairVO repairVO = new BetResultRepairVO();
        repairVO.setNoActive(noActive);
        //已开的 不能修改 开奖结果  ，未开 就用
        String yiAwardOption = resultBO.getAwardOption();
        if (StringUtils.isNotEmpty(yiAwardOption)) {
            //检查是否一样
            if (!yiAwardOption.equals(awardOption)) {
                log.error("补单----已开的 不能修改 开奖结果");
                return null;
            }
        } else {
            //未开 就用
            repairVO.setAwardOption(awardOption);
        }

        //检查是否规范
        List<TgBaccRuleEnum> list = TgBaccRuleEnum.getList();
        List<String> rules = list.stream().map(item -> item.getCode()).toList();
        String[] awardOptionArr = awardOption.toUpperCase().split(",");
        for (String option : awardOptionArr) {
            if (!rules.contains(option)) {
                log.error("补单----传入了其他玩法");
                return null;
            }
        }
        return repairVO;
    }

    private BetResultBO getBetResultBO(String noActive) {
        String url = gameServerUrl + GameServerConstants.GAME_BET_RESULT_NOACTIVE;
        url = url + "?noActive=" + noActive;
        String result = HttpClient4Util.doGet(url);
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        ResponseEntity response = JSONObject.parseObject(result, ResponseEntity.class);
        if (Objects.nonNull(response) && response.getCode() == ResponseCode.SUCCESS.getCode()) {
            JSONObject jsonObject = (JSONObject) response.getData();
            return JSONObject.parseObject(JSONObject.toJSONString(jsonObject), BetResultBO.class);
        }
        return null;
    }


}
