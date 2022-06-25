package com.baisha.backendserver.controller;

import com.alibaba.fastjson.JSON;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.model.vo.IdVO;
import com.baisha.backendserver.model.vo.user.UserPageVO;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.util.constants.BackendConstants;
import com.baisha.backendserver.util.constants.UserServerConstants;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@RestController
@RequestMapping("user")
@Api(tags = "用户管理")
public class UserController {

    @Value("${url.userServer}")
    private String userServerUrl;

    @Autowired
    private CommonService commonService;

    @GetMapping("page")
    @ApiOperation(("用户分页"))
    public ResponseEntity<Page<UserPageVO>> page(UserPageVO vo) {
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
        if (Objects.isNull(vo.getId())) {
            return ResponseUtil.parameterNotNull();
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USERDELETE;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.DELETE,
                currentUser.getUserName() + "删除用户id={" + vo.getId() + "}", BackendConstants.USER_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }

    @ApiOperation(("启用/禁用用户"))
    @PostMapping("status")
    public ResponseEntity status(IdVO vo) {
        if (Objects.isNull(vo) || null == vo.getId()) {
            return ResponseUtil.parameterNotNull();
        }
        String url = userServerUrl + UserServerConstants.USERSERVER_USERSTATUS;
        Map<String, Object> param = BackendServerUtil.objectToMap(vo);
        String result = HttpClient4Util.doPost(url, param);
        if (CommonUtil.checkNull(result)) {
            return ResponseUtil.fail();
        }
        Admin currentUser = commonService.getCurrentUser();
        log.info("{} {} {} {}", currentUser.getUserName(), BackendConstants.UPDATE,
                currentUser.getUserName() + "修改管理员状态id={" + vo.getId() + "}", BackendConstants.USER_MODULE);
        return JSON.parseObject(result, ResponseEntity.class);
    }


}
