package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baisha.backendserver.business.CommonBusiness;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.bo.sys.SysParameterBO;
import com.baisha.backendserver.model.vo.sys.SysParameterVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.GameServerConstants;
import com.baisha.modulecommon.reponse.ResponseCode;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "sys")
@Api(tags = "系统参数设置")
public class SysParamController {

    @Value("${url.gameServer}")
    private String gameServerUrl;

    @Autowired
    private CommonBusiness commonService;


    @ApiOperation("获取系统参数信息")
    @GetMapping(value = "getSysInfo")
    public ResponseEntity<SysParameterBO> getSysParameterInfo() {
        SysParameterBO bo = new SysParameterBO();
        //获取返水信息
        BigDecimal rebate = getRebate();
        if (Objects.nonNull(rebate)) {
            bo.setRebate(rebate);
        }
        return ResponseUtil.success(bo);
    }

    private BigDecimal getRebate() {
        String url = gameServerUrl + GameServerConstants.GAME_GET_REBATE_INFO;
        String result = HttpClient4Util.doGet(url);
        if (StringUtils.isEmpty(result)) {
            log.error("获取返水比例失败");
            return null;
        }
        ResponseEntity responseEntity = JSONObject.parseObject(result, ResponseEntity.class);
        if (responseEntity.getCode() == ResponseCode.SUCCESS.getCode()) {
            String data = (String) responseEntity.getData();
            return new BigDecimal(data);
        }
        log.error("获取返水比例失败");
        return null;
    }


    @ApiOperation("设置系统参数信息")
    @PostMapping(value = "setSysInfo")
    public ResponseEntity setSysParameterInfo(SysParameterVO vo) {
        if (SysParameterVO.checkRebate(vo.getRebate())) {
            return new ResponseEntity("返水比例不规范");
        }
        //返水设置
        setRebate(vo.getRebate());
        //获取当前登陆用户
        Admin currentUser = commonService.getCurrentUser();
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                JSON.toJSONString(vo), BackendConstants.SYS_PARAMTER_MODULE);
        return ResponseUtil.success();
    }


    private void setRebate(BigDecimal rebate) {
        String url = gameServerUrl + GameServerConstants.GAME_SET_REBATE_INFO;
        Map<String, Object> param = new HashMap<>(16);
        param.put("returnAmountMultiplier", rebate);
        String result = HttpClient4Util.doPost(url, param);
        if (StringUtils.isEmpty(result)) {
            log.error("设置返水比例失败");
        }
    }

}
