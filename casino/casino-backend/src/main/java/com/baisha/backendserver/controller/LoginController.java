package com.baisha.backendserver.controller;


import com.baisha.backendserver.bo.admin.LoginBO;
import com.baisha.backendserver.business.CommonService;
import com.baisha.backendserver.model.Admin;
import com.baisha.backendserver.service.AdminService;
import com.baisha.backendserver.util.BackendServerUtil;
import com.baisha.backendserver.vo.login.LoginVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.annotation.NoAuthentication;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulejjwt.JjwtUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@RestController
@Api(tags = "登陆")
public class LoginController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AdminService adminService;


    @ApiOperation(("管理员登陆"))
    @PostMapping("login")
    @NoAuthentication
    public ResponseEntity addAdmin(LoginVO vo) {
        if (Objects.isNull(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        if (Admin.checkUserName(vo.getUserName())) {
            return new ResponseEntity("用户名不规范");
        }
        if (Admin.checkPassword(vo.getPassword())) {
            return new ResponseEntity("密码不规范");
        }
        // 查询用户名是否存在
        Admin user = adminService.findByUserNameSql(vo.getUserName());
        if (Objects.isNull(user)) {
            return new ResponseEntity("帐号或密码错误");
        }
        //已经删除或者禁用
        if (!Constants.open.equals(user.getStatus())) {
            return new ResponseEntity("该帐号不可操作");
        }
        //验证密码
        String bcryptPassword = user.getPassword();
        boolean bcrypt = BackendServerUtil.checkBcrypt(vo.getPassword(), bcryptPassword);
        if (!bcrypt) {
            return new ResponseEntity("帐号或密码错误");
        }
        JjwtUtil.Subject subject = new JjwtUtil.Subject();
        subject.setUserId(String.valueOf(user.getId()));
        subject.setBcryptPassword(user.getPassword());
        String token = JjwtUtil.generic(subject, Constants.CASINO_ADMIN);
        setUserTokenToRedis(user.getId(), token);
        return ResponseUtil.success(LoginBO.builder().id(user.getId()).userName(user.getUserName()).nickName(user.getNickName()).token(token).build());
    }

    private void setUserTokenToRedis(Long userId, String token) {
        JjwtUtil.Token jwtToken = new JjwtUtil.Token();
        jwtToken.setOldToken(token);
        redisUtil.set(Constants.REDIS_TOKEN_ADMIN + userId, jwtToken, JjwtUtil.ttl + Constants.ADMIN_REFRESH_TTL);
    }

    @ApiOperation(("管理直接登陆(测试)"))
    @PostMapping("login/test")
    @NoAuthentication
    public ResponseEntity addAdminTest() {
        LoginVO vo = LoginVO.builder().userName("buyaodong").password("abcd1234").build();
        return addAdmin(vo);
    }


}
