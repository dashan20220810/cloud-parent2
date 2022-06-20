package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.constants.UserServerConstants;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.vo.IdVO;
import com.baisha.backendserver.vo.StatusVO;
import com.baisha.backendserver.vo.user.UserPageVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * @author yihui
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户管理")
public class UserController {

    @Value("${url.userServer}")
    private String userServerUrl;


    @GetMapping("page")
    @ApiOperation(("用户分页"))
    public ResponseEntity page(UserPageVO vo) {
        StringBuffer sb = new StringBuffer();
        sb.append(userServerUrl + UserServerConstants.USERSERVER_USERPAGE + "?pageNumber=" + vo.getPageNumber() +
                "&pageSize=" + vo.getPageSize());
        if (StringUtils.isNotBlank(vo.getUserName())) {
            sb.append("&userName=" + vo.getUserName());
        }
        String url = sb.toString();
        String result = HttpClient4Util.doGet(url);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }


    @ApiOperation(("删除用户"))
    @PostMapping("delete")
    public ResponseEntity delete(IdVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USERDELETE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation(("启用/禁用用户"))
    @PostMapping("status")
    public ResponseEntity status(StatusVO vo) {
        if (Objects.isNull(vo) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        if (BackendServerUtil.checkStatus(vo.getStatus())) {
            return new ResponseEntity("状态不规范");
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USERSTATUS;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        return JSON.parseObject(result, ResponseEntity.class);
    }


}
